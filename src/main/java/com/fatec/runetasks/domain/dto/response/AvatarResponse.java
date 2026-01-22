package com.fatec.runetasks.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AvatarResponse {

    private Long id;

    private String title;

    private String iconName;

    private String icon;

    private int price;

    private boolean owned;

}
