val bitmap = drawable.bitmap
val hash = simpleIconHash(bitmap)

Log.d("NotificationCatcher", """
    圖標信息:
    寬度: ${bitmap.width}
    高度: ${bitmap.height}
    區域亮度哈希值: $hash
""".trimIndent())

// 更新 ViewModel 中的哈希值
viewModel.setLastIconHash(hash, bitmap) 