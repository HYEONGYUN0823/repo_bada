package com.a7a7.module.sea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeaService {

	@Autowired
	SeaDao dao;
}
