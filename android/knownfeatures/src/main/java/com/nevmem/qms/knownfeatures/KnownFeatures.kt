package com.nevmem.qms.knownfeatures

enum class KnownFeatures(val value: String, val textDescriptionResource: Int) {
    QrCodeScanningEnabled("qr_code_scanner_enabled", R.string.qr_code_feature),
    VisitedTags("visited_tags_visible", R.string.visited_tags_visible_features),
    UseAnimationsOnProfilePage("profile_page_use_animations", R.string.profile_page_use_animations),
    ShowHistoryOnProfilePage("profile_page_history", R.string.profile_page_history),
    UseCardsForSuggestsOnStatusPage("use_cards_for_suggests_on_status_page", R.string.use_cards_for_suggests_on_status_page),
    UseAnimationsOnJoinPage("use_animation_on_join_page", R.string.use_animation_on_join_page),
    RatingsForServices("rating_for_services", R.string.ratings_for_services),
    RatingsForOrganizations("rating_for_organizations", R.string.ratings_for_organizations),
    EnableOrganizationSharing("enable_organizations_sharing", R.string.enable_organizations_sharing)
}
