package com.example.aipartner.pojo.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 辅助设备实体
 * 对应表：db_aipartner.auxiliary_device
 *
 * 字段说明：
 * - state：设备状态，字符串形式（打开："true"，关闭："false"）
 * - assistDeviceName：辅助学习设备名字
 * - deviceId：外键，设备 dev_id
 * - imgUrl：辅助设备图片地址
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuxiliaryDevice {
    private Integer id;
    private String state;
    private String assistDeviceName;
    private String deviceId;
    private String imgUrl;
    private String auxiliaryDeviceId;
}