package com.quoren.app.data.audio

import androidx.media3.common.C
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheEvictor
import androidx.media3.datasource.cache.CacheSpan
import androidx.media3.datasource.cache.SimpleCache
import com.quoren.app.domain.repository.AudioCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TreeSet
import javax.inject.Inject
import javax.inject.Singleton

/** LRU evictor whose size limit can change at runtime. */
class ConfigurableLruCacheEvictor(@Volatile var maxBytes: Long) : CacheEvictor {
    private val spans = TreeSet<CacheSpan> { a, b ->
        val diff = a.lastTouchTimestamp - b.lastTouchTimestamp
        if (diff == 0L) a.compareTo(b) else if (diff < 0) -1 else 1
    }
    private var currentSize = 0L
    private var initialized = false

    override fun requiresCacheSpanTouches() = true

    override fun onCacheInitialized() = Unit

    override fun onStartFile(cache: Cache, key: String, position: Long, length: Long) {
        initializeFromDisk(cache)
        if (length != C.LENGTH_UNSET.toLong()) evict(cache, length)
    }

    override fun onSpanAdded(cache: Cache, span: CacheSpan) {
        initializeFromDisk(cache)
        if (spans.add(span)) currentSize += span.length
        evict(cache, 0)
    }

    override fun onSpanRemoved(cache: Cache, span: CacheSpan) {
        initializeFromDisk(cache)
        if (spans.remove(span)) currentSize -= span.length
    }

    override fun onSpanTouched(cache: Cache, oldSpan: CacheSpan, newSpan: CacheSpan) {
        initializeFromDisk(cache)
        if (spans.remove(oldSpan)) currentSize -= oldSpan.length
        if (spans.add(newSpan)) currentSize += newSpan.length
    }

    @Synchronized
    private fun initializeFromDisk(cache: Cache) {
        if (initialized) return
        cache.keys.forEach { key ->
            cache.getCachedSpans(key).forEach { span ->
                if (spans.add(span)) currentSize += span.length
            }
        }
        initialized = true
        evict(cache, 0)
    }

    private fun evict(cache: Cache, requiredSpace: Long) {
        while (currentSize + requiredSpace > maxBytes && spans.isNotEmpty()) cache.removeSpan(spans.first())
    }
}

@Singleton
class AudioCacheManagerImpl @Inject constructor(
    private val cache: SimpleCache,
    private val evictor: ConfigurableLruCacheEvictor,
) : AudioCacheManager {

    override suspend fun cacheSizeBytes(): Long = withContext(Dispatchers.IO) { cache.cacheSpace }

    override suspend fun clear() {
        withContext(Dispatchers.IO) { cache.keys.toList().forEach { cache.removeResource(it) } }
    }

    override fun setLimitMb(limitMb: Int) {
        evictor.maxBytes = limitMb.toLong() * 1024 * 1024
    }
}
