package br.com.complianceit.services;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.api.services.NfServService;
import br.com.cadastroit.services.repositories.NfServRepository;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
class CstMonitoramentonfsApiApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private NfServService nfServService;
	
	@Autowired
	private NfServRepository nfServRepository;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		
	}
	
	@Test
	@Order(11)
	void handlePostEmpresaAtivCprbAllFieldsNullAndReturnBlockException() {
		
	}
	
//	 @Test
//	    public void testBuscarPorStatusDmStProc() {
//	        // Create a mock of the `NfServService` class.
//	        NfServService mockNfServService = mock(NfServService.class);
//
//	        // Create a `FiltersDto` object.
//	        FiltersDto filtersDto = new FiltersDto();
//	        filtersDto.setMultOrgCd("123456");
//
//	        // Mock the `vDashTotalizadorDmStProc()` method to return a list of `FiltersDto` objects.
//	        List<FiltersDto> listDto = Arrays.asList(new FiltersDto(), new FiltersDto());
//	        when(mockNfServService.vDashTotalizadorDmStProc(filtersDto, any())).thenReturn(listDto);
//	        ResponseEntity<Object> responseEntity = BuscarPorStatusDmStProc.buscarPorStatusDmStProc(filtersDto, mockNfServService);
//	        assertNotNull(responseEntity);
//	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//	        assertTrue(responseEntity.getBody() instanceof List);
//	        assertEquals(2, ((List<?>) responseEntity.getBody()).size());
//	    }
	
	public void invokeGetter(Object... o) {
		Stream.of(o).forEach(obj -> {
			log.info("Object Name = " + obj.getClass().getSimpleName());
			Stream.of(obj.getClass().getDeclaredMethods()).filter(m -> m.getName().contains("get")).forEach(m -> {
				try {
					if (m.invoke(obj) != null)
						log.info(m.invoke(obj).toString());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					log.error("Error on execution method,[error] = " + e.getMessage());
				}
			});
		});
	}
}
