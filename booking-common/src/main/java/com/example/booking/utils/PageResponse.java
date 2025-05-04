package com.example.booking.utils;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResponse<T> {

	private Long totalRecord;
	private int totalPage;
	private int currentSize;
	private int currentPage;
	private List<T> dtoList;

	public static <T> PageResponse<T> from(Page<T> dtoList) {
		return new PageResponse<T>(dtoList.getTotalElements(), dtoList.getTotalPages(), dtoList.getSize(),
				dtoList.getNumber(), dtoList.getContent());
	}
}
