package com.woomulwoomul.core.common.converter

import com.woomulwoomul.core.common.constant.BackgroundColor
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter


@Converter(autoApply = true)
class BackgroundColorConverter : AttributeConverter<BackgroundColor, String> {

    override fun convertToDatabaseColumn(attribute: BackgroundColor?): String? {
        return attribute?.value
    }

    override fun convertToEntityAttribute(dbData: String?): BackgroundColor? {
        return dbData?.let { BackgroundColor.of(it) }
    }
}