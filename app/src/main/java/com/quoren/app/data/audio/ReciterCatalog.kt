package com.quoren.app.data.audio

import com.quoren.app.domain.model.Reciter

/** Static reciter catalog. Base URLs point at EveryAyah per-ayah files named SSSAAA.mp3. */
object ReciterCatalog {
    private const val BASE = "https://everyayah.com/data"
    private const val RIWAYA_HAFS = "حفص عن عاصم"

    const val DEFAULT_ID = "alafasy"

    val all: List<Reciter> = listOf(
        Reciter("alafasy", "مشاري راشد العفاسي", "Mishary Alafasy", RIWAYA_HAFS, null, "$BASE/Alafasy_128kbps", "$BASE/Alafasy_64kbps"),
        Reciter("maher", "ماهر المعيقلي", "Maher Al Muaiqly", RIWAYA_HAFS, null, "$BASE/MaherAlMuaiqly128kbps", "$BASE/Maher_AlMuaiqly_64kbps"),
        Reciter(
            "ajmi", "أحمد بن علي العجمي", "Ahmed Al Ajmi", RIWAYA_HAFS, null,
            "$BASE/Ahmed_ibn_Ali_al-Ajamy_128kbps_ketaballah.net", "$BASE/Ahmed_ibn_Ali_al-Ajamy_64kbps_QuranExplorer.Com",
        ),
        Reciter("dosari", "ياسر الدوسري", "Yasser Al Dosari", RIWAYA_HAFS, null, "$BASE/Yasser_Ad-Dussary_128kbps"),
        Reciter("ghamdi", "سعد الغامدي", "Saad Al Ghamdi", RIWAYA_HAFS, null, "$BASE/Ghamadi_40kbps"),
        Reciter(
            "abdulbasit", "عبد الباسط عبد الصمد", "Abdul Basit", RIWAYA_HAFS, null,
            "$BASE/Abdul_Basit_Murattal_192kbps", "$BASE/Abdul_Basit_Murattal_64kbps",
        ),
        Reciter("minshawi", "محمد صديق المنشاوي", "Al Minshawi", RIWAYA_HAFS, null, "$BASE/Minshawy_Murattal_128kbps"),
        Reciter("jaber", "علي جابر", "Ali Jaber", RIWAYA_HAFS, null, "$BASE/Ali_Jaber_64kbps"),
    )

    fun ayahFileName(surahId: Int, ayahNumber: Int): String = "%03d%03d.mp3".format(surahId, ayahNumber)

    fun ayahUrl(baseUrl: String, surahId: Int, ayahNumber: Int): String = "$baseUrl/${ayahFileName(surahId, ayahNumber)}"

    fun needsBasmalaIntro(surahId: Int, startAyah: Int): Boolean =
        startAyah == 1 && surahId != 1 && surahId != 9
}
