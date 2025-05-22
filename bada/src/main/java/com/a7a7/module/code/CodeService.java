package com.a7a7.module.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import jakarta.annotation.PostConstruct;

@Service
public class CodeService {
	
	@Autowired
	CodeDao dao;
	
	private Map<String, List<CodeDto>> codeMap;
	
	// 캐싱
    @PostConstruct
    public void loadCodeCache() {
        List<CodeDto> codes = dao.findAllCodes();

        codeMap = new HashMap<>();

        for (CodeDto code : codes) {
            String codegroupName = code.getCodegroupName();
            if (!codeMap.containsKey(codegroupName)) {
                codeMap.put(codegroupName, new ArrayList<>());
            }
            codeMap.get(codegroupName).add(code);
        }
    }

    public List<CodeDto> getCodesByCodegroupName(String codegroupName) {
        return codeMap.getOrDefault(codegroupName, Collections.emptyList());
    }

}
