package com.woomulwoomul.core.domain.question

import com.woomulwoomul.core.common.constant.ExceptionCode.QUESTION_BACKGROUND_COLOR_PATTERN_INVALID
import com.woomulwoomul.core.common.response.CustomException

enum class BackgroundColor(
    val value: String,
) {
    LIGHT_SALMON_PINK("#FFACA8"),
    LIGHT_ORANGE("#FFA34F"),
    GOLDEN_YELLOW("#FFC34F"),
    PALE_GREEN("#C5FFAA"),
    BRIGHT_TURQUOISE("#1AE7D8"),
    SKY_BLUE("#4FB5FF"),
    PERIWINKLE_BLUE("#868BFF"),
    LAVENDER_PURPLE("#C58AFF"),
    LIGHT_PURPLE("#FF9CE3"),
    WHITE("#FFFFFF");

    companion object {
        fun of(value: String): BackgroundColor {
            return entries.find { it.value == value }
                ?: throw CustomException(QUESTION_BACKGROUND_COLOR_PATTERN_INVALID)
        }
    }
}