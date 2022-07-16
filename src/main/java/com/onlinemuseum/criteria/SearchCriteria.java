package com.onlinemuseum.criteria;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SearchCriteria {

    private Integer pageNumber = 0;

    private Integer pageSize = 25;

    private String sortField;

    private String sortDirection;
}
