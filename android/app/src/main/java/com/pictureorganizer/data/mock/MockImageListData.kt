package com.pictureorganizer.data.mock

import com.pictureorganizer.model.ImageListItem
import com.pictureorganizer.model.ImageStatus
import com.pictureorganizer.util.file.toDirName

object MockImageListData {
    private val placeholderColors =
        listOf(
            0xFFE57373,
            0xFF81C784,
            0xFF64B5F6,
            0xFFFFB74D,
            0xFFBA68C8,
            0xFF4DD0E1,
            0xFFAED581,
            0xFFFF8A65,
            0xFF7986CB,
            0xFF4DB6AC,
            0xFFF06292,
            0xFF9575CD,
        )

    fun pendingItems(): List<ImageListItem> =
        listOf(
            item("pending-01", "2026-08-05", "周末出游照片，待重命名", listOf("待处理", "旅行"), 0, ImageStatus.Pending),
            item("pending-02", "2026-08-06", "扫描件截图，需加标签", listOf("待处理"), 1, ImageStatus.Pending),
            item("pending-03", "2026-08-07", "产品参考图，文件名混乱", listOf("待处理", "工作"), 2, ImageStatus.Pending),
            item("pending-04", "2026-08-08", "朋友聚会合影", listOf("待处理", "社交"), 3, ImageStatus.Pending),
            item("pending-05", "2026-08-09", "菜谱截图，待分类", listOf("待处理", "生活"), 4, ImageStatus.Pending),
            item("pending-06", "2026-08-10", "发票照片，需确认信息", listOf("待处理", "票据"), 5, ImageStatus.Pending),
            item("pending-07", "2026-08-12", "风景照，分辨率过大待压缩", listOf("待处理"), 6, ImageStatus.Pending),
            item("pending-08", "2026-08-14", "设计稿导出，待整理", listOf("待处理", "工作"), 7, ImageStatus.Pending),
            item("pending-09", "2026-08-16", "宠物日常照片", listOf("待处理", "宠物"), 8, ImageStatus.Pending),
            item("pending-10", "2026-08-18", "会议白板拍照", listOf("待处理", "工作"), 9, ImageStatus.Pending),
            item("pending-11", "2026-08-22", "网购商品图，待删背景", listOf("待处理", "购物"), 10, ImageStatus.Pending),
            item("pending-12", "2026-08-25", "旧相册导入，未分类", listOf("待处理"), 11, ImageStatus.Pending),
        )

    fun confirmedItems(): List<ImageListItem> =
        listOf(
            item("confirmed-01", "2026-08-03", "已重命名为「2026-春游-01」", listOf("已确认", "旅行"), 0, ImageStatus.Confirmed),
            item("confirmed-02", "2026-08-04", "已添加标签「工作」", listOf("已确认", "工作"), 1, ImageStatus.Confirmed),
            item("confirmed-03", "2026-08-06", "压缩后保存，原图 4.2MB", listOf("已确认", "已压缩"), 2, ImageStatus.Confirmed),
            item("confirmed-04", "2026-08-07", "证件照已整理完毕", listOf("已确认", "证件"), 3, ImageStatus.Confirmed),
            item("confirmed-05", "2026-08-09", "美食照片已分类", listOf("已确认", "生活"), 4, ImageStatus.Confirmed),
            item("confirmed-06", "2026-08-11", "项目截图已标注版本号", listOf("已确认", "工作"), 5, ImageStatus.Confirmed),
            item("confirmed-07", "2026-08-13", "家人合影已重命名", listOf("已确认", "家庭"), 6, ImageStatus.Confirmed),
            item("confirmed-08", "2026-08-15", "健身记录照片已归档", listOf("已确认", "运动"), 7, ImageStatus.Confirmed),
            item("confirmed-09", "2026-08-17", "读书笔记配图已整理", listOf("已确认", "阅读"), 8, ImageStatus.Confirmed),
            item("confirmed-10", "2026-08-19", "装修参考图已加标签", listOf("已确认", "家居"), 9, ImageStatus.Confirmed),
            item("confirmed-11", "2026-08-21", "活动海报已确认完成", listOf("已确认", "活动"), 10, ImageStatus.Confirmed),
            item("confirmed-12", "2026-08-24", "旅行纪念品照片已整理", listOf("已确认", "旅行"), 11, ImageStatus.Confirmed),
        )

    fun noModifyItems(): List<ImageListItem> =
        listOf(
            item("nomodify-01", "2026-08-02", "原始备份，无需修改", listOf("备份"), 0, ImageStatus.NoModify),
            item("nomodify-02", "2026-08-04", "系统默认壁纸截图", emptyList(), 1, ImageStatus.NoModify),
            item("nomodify-03", "2026-08-05", "临时缓存图片，保持原样", listOf("缓存"), 2, ImageStatus.NoModify),
            item("nomodify-04", "2026-08-07", "已归档历史照片", listOf("归档"), 3, ImageStatus.NoModify),
            item("nomodify-05", "2026-08-08", "参考素材，不纳入整理", listOf("素材"), 4, ImageStatus.NoModify),
            item("nomodify-06", "2026-08-10", "第三方分享原图", emptyList(), 5, ImageStatus.NoModify),
            item("nomodify-07", "2026-08-12", "只读副本，禁止改动", listOf("副本"), 6, ImageStatus.NoModify),
            item("nomodify-08", "2026-08-14", "测试导入图片", listOf("测试"), 7, ImageStatus.NoModify),
            item("nomodify-09", "2026-08-16", "低优先级，暂不处理", emptyList(), 8, ImageStatus.NoModify),
            item("nomodify-10", "2026-08-18", "水印样例图", listOf("样例"), 9, ImageStatus.NoModify),
            item("nomodify-11", "2026-08-20", "已标记为跳过的图片", listOf("跳过"), 10, ImageStatus.NoModify),
            item("nomodify-12", "2026-08-23", "外部链接保存图", emptyList(), 11, ImageStatus.NoModify),
        )

    private fun item(
        id: String,
        date: String,
        description: String,
        tags: List<String>,
        colorIndex: Int,
        status: ImageStatus,
    ): ImageListItem =
        ImageListItem(
            id = id,
            date = date,
            description = description,
            // S1：tags 仅用户标签；预览数据剔除状态词，模拟当前数据形态
            tags = tags.filter { it !in ImageListItem.STATUS_TAGS },
            placeholderColorArgb = placeholderColors[colorIndex % placeholderColors.size],
            status = status,
            filePath = "${status.toDirName()}/$id.jpg",
        )
}
