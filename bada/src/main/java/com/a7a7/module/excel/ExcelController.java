package com.a7a7.module.excel;

import java.io.IOException;
import java.util.List;

import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.a7a7.module.sea.SeaDto;
import com.a7a7.module.sea.SeaService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ExcelController {
	
	@Autowired
	SeaService seaService;

	@GetMapping("/admin/export")
	public void exportToExcel(HttpServletResponse response) throws IOException { //HttpServletResponse response:  Spring이 제공하는 객체로, 사용자의 브라우저에 응답을 직접 보낼 수 있게 해줍니다., 보통 HTML 텍스트를 보내기도 하지만, 여기선 **파일(엑셀 파일)**을 보내는 용도
	    // 파일 이름 및 타입 설정
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); //이 응답은 엑셀(xlsx) 파일: 브라우저는 받은 응답이 어떤 종류의 데이터인지 알아야 합 -> application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" 는 .xlsx 형식의 표준
	    response.setHeader("Content-Disposition", "attachment; filename=export.xlsx"); //"Content-Disposition" 헤더는 브라우저가 이걸 "다운로드할 파일"로 처리하라고 지시, attachment" → 이걸 브라우저에 바로 표시하지 말고, 다운로드하게 함,
	    																				//filename=export.xlsx" → 다운로드될 때 기본 파일 이름이 export.xlsx가 되도록 설정
	    // DB에서 데이터 가져오기 (예: user 리스트)
	    List<SeaDto> travelList = seaService.seaList();

	    // 엑셀 생성
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Users");

	    // 헤더 행 작성
	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("SEQ");
	    headerRow.createCell(1).setCellValue("바다여행장소(sareaDtlNm)");
	    headerRow.createCell(2).setCellValue("위도(lat)");
	    headerRow.createCell(3).setCellValue("경도(lot)");

	    // 데이터 채우기
	    int rowNum = 1;
	    for (SeaDto travel : travelList) {
	        Row row = sheet.createRow(rowNum++);
	        row.createCell(0).setCellValue(travel.getSeaId());
	        row.createCell(1).setCellValue(travel.getSareaDtlNm());
	        row.createCell(2).setCellValue(travel.getLat());
	        row.createCell(3).setCellValue(travel.getLot());
	    }

	    // 파일 응답으로 전송
	    workbook.write(response.getOutputStream());
	    workbook.close();
	}
	
	
	
}
