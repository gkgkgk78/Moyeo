package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface MoyeoPostStatusDto {
	Long getIsAnyDeleted();
	Long getIsAllPublic();
}