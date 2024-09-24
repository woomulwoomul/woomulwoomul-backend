package com.woomulwoomul.core.common.constant

class RegexConstants {

    companion object {
        const val LOWERCASE_ENG_KOR_NUMBER_UNDERSCORE = "^[a-z0-9_가-힣]+$"
        const val ACTIVE_OR_ADMIN_DEL = "^(ACTIVE|ADMIN_DEL)$"
        const val SERVICE_STATUS = "^(ACTIVE|USER_DEL|ADMIN_DEL)$"
        const val BACKGROUND_COLOR_CODE = "^(FFACA8|FFA34F|FFC34F|C5FFAA|1AE7D8|4FB5FF|868BFF|C58AFF|FF9CE3|FFFFFF)$"
    }
}