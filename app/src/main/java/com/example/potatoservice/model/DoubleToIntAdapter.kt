package com.example.potatoservice.model

import com.google.gson.*
import java.lang.reflect.Type

class DoubleToIntAdapter : JsonDeserializer<Int> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
        // json이 null이거나 비어있는 경우 0을 반환
        if (json == null || json.isJsonNull) return 0

        // json 값이 Double 타입인 경우 Int로 변환
        return if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
            json.asDouble.toInt() // Double을 Int로 변환
        } else {
            0
        }
    }
}
