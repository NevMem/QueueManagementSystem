package com.nevmem.qms.knownfeatures

enum class KnownFeatures(val value: String, val textDescriptionResource: Int) {
    QrCodeScanningEnabled("qr_code_scanner_enabled", R.string.qr_code_feature),
    VisitedTags("visited_tags_visible", R.string.visited_tags_visible_features),
}
