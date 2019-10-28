package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tbl_block_height")
@Data
public class BlockHeight {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private Integer height;

}
