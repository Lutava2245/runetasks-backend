package com.fatec.runetasks.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RewardResponse {

    private Long id;

    private String title;

    private String description;

	private int price;

	private String status;

}
