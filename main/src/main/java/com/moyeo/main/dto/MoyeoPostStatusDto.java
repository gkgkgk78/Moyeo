package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoyeoPostStatusDto {
	private Boolean isAnyDeleted;
	private Boolean isAllPublic;
}
