package br.com.cadastroit.services.web.controllers;


import static java.util.stream.Collectors.summingLong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.HealthCheck;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.api.services.NfServService;
import br.com.cadastroit.services.commons.api.DetalhamentoTotalEstadoCidade;
import br.com.cadastroit.services.commons.api.DetalhamentoTotalPais;
import br.com.cadastroit.services.commons.api.DetalhamentoTotalPorCidade;
import br.com.cadastroit.services.commons.api.DetalhamentoTotalPorEstado;
import br.com.cadastroit.services.commons.api.NfServCommons;
import br.com.cadastroit.services.commons.api.ViewCidadeEmpresa;
import br.com.cadastroit.services.commons.api.ViewTotalEstado;
import br.com.cadastroit.services.commons.api.ViewTotalItemNfServ;
import br.com.cadastroit.services.commons.api.ViewTotalItemNfServDetalh;
import br.com.cadastroit.services.exceptions.NfServException;
import br.com.cadastroit.services.repositories.CommonsRepository;
import br.com.cadastroit.services.repositories.NfServRepository;
import br.com.cadastroit.services.web.controller.commons.CommonsValidation;
import br.com.cadastroit.services.web.controller.commons.NfServCommonsController;
import br.com.cadastroit.services.web.dto.Filters;
import br.com.cadastroit.services.web.dto.NfServDto;
import br.com.cadastroit.services.web.dto.StatusDmStProcDto;
import br.com.cadastroit.services.web.mapper.NfServMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/administracao/nfserv")
@RequiredArgsConstructor
public class NfServController {

    private final String EMPTY_MSG = "List is empty...";
    private final EntityManagerFactory entityManagerFactory;
    private final CommonsRepository commonsRepository;
    private final NfServRepository nfServRepository;
    private final NfServService nfServService;
    private final NfServCommonsController nfServCommonsController;
    
    private final NfServMapper nfServMapper = Mappers.getMapper(NfServMapper.class);

    @ApiOperation(value = "Get status from API")
    @GetMapping("/status")
    public ResponseEntity<Object> status(@RequestParam(name = "status", required = false, defaultValue = "UP") String status) {
        try {
            return new ResponseEntity<>(HealthCheck.builder().status(status)
                    .maxId(nfServRepository.maxId(entityManagerFactory)).build(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get maxId NfServ ")
    @GetMapping("/maxId")
    public ResponseEntity<Object> maxId(@RequestHeader("empresaId") Long empresaId) {
        try {
            Long id = nfServRepository.maxId(entityManagerFactory, empresaId);
            return ResponseEntity.ok(id);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
        }
    }

    @ApiOperation(value = "Get NfServ by Id")
    @GetMapping("/find/{id}")
    public ResponseEntity<Object> find(@PathVariable("id") Long id) {

        CommonsValidation commonsValidation = this.createCommonsValidation();

        try {
            NfServ entity = nfServRepository.findById(id, entityManagerFactory);
            return ResponseEntity.ok().body(this.nfServCommonsController.mountDto(this.nfServMapper::toDto, entity));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
        }
    }
    
    @ApiOperation(value = "Get NfServ by Id")
    @GetMapping("/findByMultOrg/{id}")
    public ResponseEntity<Object> find(@RequestHeader("multOrgCd") String multOrgCd, @PathVariable("id") Long id) {

        CommonsValidation commonsValidation = this.createCommonsValidation();

        try {
            NfServ entity = nfServRepository.findById(multOrgCd, id, entityManagerFactory);
            return ResponseEntity.ok().body(this.nfServCommonsController.mountDto(nfServMapper::toDto, entity));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
        }
    }

    @ApiOperation(value = "Create a new NfServ record")
    @PostMapping("/create")
    public ResponseEntity<Object> handlePost(@RequestHeader("empresaId") Long empresaId,
                                             @Validated @RequestBody NfServDto entityDto) {

        CommonsValidation commonsValidation = this.createCommonsValidation();

        try {
            return mountEntity(entityDto, null, null, false);
        } catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
        }
    }

    @ApiOperation(value = "Updating a NfServ record")
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> handleUpdate(@PathVariable("id") Long id, @RequestHeader("empresaId") Long empresaId,
                                               @Validated @RequestBody NfServDto entityDto) {
        CommonsValidation commonsValidation = this.createCommonsValidation();

        try {
            return mountEntity(entityDto, id, null, true);
        } catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
        }
    }

    @ApiOperation(value = "Deleting a NfServ record")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) {

        CommonsValidation commonsValidation = this.createCommonsValidation();

        try {
            nfServRepository.delete(nfServRepository.findById(empresaId, id, entityManagerFactory));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(commonsValidation.createResponseTemplate(String.format(ex.getMessage())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get all records using pagination mechanism")
    @GetMapping("/all/{page}/{length}")
    public ResponseEntity<Object> all(@RequestParam(required = false) Map<String, Object> requestParams,
                                      @PathVariable("page") int page, @PathVariable("length") int length) {

        try {
            Long count = nfServRepository.count(null, new NfServDto(), entityManagerFactory);
            if (count > 0) {
                List<NfServ> list = nfServRepository.findAll(entityManagerFactory, requestParams, page, length);
                HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
                return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(nfServMapper::toDto, list));

            }
            return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    //	ESTE ENDPOINT VAI ATENDER O PAINEL SUPERIOR PARA TRAZER SEPARADO POR DMSTPROC
    @ApiOperation(value = "Get NfServ by DmStProc")
    @PostMapping("/buscarPorStatusDmStProc")
    public ResponseEntity<Object> buscarPorStatusDmStProc(@RequestBody Filters filtersDto) {

    	CommonsValidation commonsValidation = this.createCommonsValidation();

    	// Map<String, Integer> map = new HashMap<>();
    	// map.put("OITO", 8);
    	// map.put("QUATRO", 4);
    	// map.put("DEZ", 10);
    	// map.put("DOIS", 2);

    	// List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
    	// Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

    	// map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(System.out::println);
    	// map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);

    	// map.entrySet().stream()
    	// .sorted(Map.Entry.comparingByKey(Comparator.comparing(Employee::getDept)))
    	// .forEach(obj -> {
    	//String tmp = obj.getKey().getDept();               	
    	// });

    	try {

    		List<Filters> listDto = this.nfServService.vDashTotalizadorDmStProc(filtersDto, entityManagerFactory);
    		if (listDto.size() > 0) {
    			
    			Long getQtdProcess = listDto.stream()
    					.collect(summingLong(obj -> obj.getQtdProcess()));
    			
    			Long getQtdPendenc = listDto.stream()
    					.collect(summingLong(obj -> obj.getQtdPendenc()));
    			
    			Long getQtdAutorizada = listDto.stream()
    					.collect(summingLong(obj -> obj.getQtdAutoriz()));
    			
    			Long getQtdCancelada = listDto.stream()
    					.collect(summingLong(obj -> obj.getQtdCancel()));
    			
    			String mO = listDto.stream().findFirst().get().getMultOrgCd();             

    			HttpHeaders headers = this.nfServCommonsController.httpHeaders(this.commonsRepository
    					.getUtilities().sumTotalValues(
    							getQtdProcess,getQtdPendenc,getQtdAutorizada,getQtdCancelada).toString());
    			
    			return ResponseEntity.ok().headers(headers)
    					.body(mountDto(Arrays.asList(
    							getQtdProcess,
    							getQtdPendenc,
    							getQtdAutorizada,
    							getQtdCancelada)));
    			
    			//return ResponseEntity.ok().headers(headers)
    			//		.body(mountDto(Arrays.asList(
    			//				this.commonsRepository.getUtilities().getValuesFromKeyMaps(mO, sumQtdProcess),
    			//				this.commonsRepository.getUtilities().getValuesFromKeyMaps(mO, sumQtdPendenc),
    			//				this.commonsRepository.getUtilities().getValuesFromKeyMaps(mO, sumQtdAutorizada),
    			//				this.commonsRepository.getUtilities().getValuesFromKeyMaps(mO, sumQtdCancelada))));
    		}
    		return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(String.valueOf(listDto.size()))).body(EMPTY_MSG);
    	} catch (Exception ex) {
    		return new ResponseEntity<>(commonsValidation.createResponseTemplate(String.format(ex.getMessage())),
    				HttpStatus.OK);
    	}
    }
    
    // ESTE ENDPOINT VAI ATENDER O PAINEL SUPERIOR LATERAL DIREITO PARA DETALHAR OS VALORES DO ITEM
    @ApiOperation(value = "Detalhar Itens da Nota e valores totais")
    @PostMapping("/buscarPorFiltros/vDashTotItemNfserv/{page}/{length}")
    public ResponseEntity<Object> vDashTotItemNfserv(@PathVariable("page") int page, @PathVariable("length") int length,
    		@RequestParam(required = false) Map<String, String> requestParams, @RequestBody Filters filtersDto) {


        Integer status = System.getenv("PROC") != null
                ? Integer.parseInt(System.getenv("PEND")) : 0;
        
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityManager managers = entityManagerFactory.createEntityManager();

        AtomicReference<ViewTotalItemNfServDetalh> viewTotalDetlh = new AtomicReference<>(new ViewTotalItemNfServDetalh());
        CommonsValidation commonsValidation = this.createCommonsValidation();

        try {

            Long count = nfServRepository.count(filtersDto, requestParams, page,
                    length, true, entityManager, managers);

            if (count > 0) {

                List<ViewTotalItemNfServ> listDto = this.nfServRepository.vDashTotItemNfServ(filtersDto, requestParams,
                        page, length, entityManagerFactory);
                
                //Long totalOfItem = listDto.stream().distinct().count();

                Stream<ViewTotalItemNfServ> stream									     = listDto.stream();
                Map<String, List<String>> getData										 = this.commonsRepository.getUtilities().getData(listDto);
                Set<String> getUfs														 = this.commonsRepository.getUtilities().getUfs(getData);             
                Map<String, List<String>> getQtdCidades									 = this.commonsRepository.getUtilities().getQtdCidades(listDto);
                
                Map<String, Double> getVlTotServ = this.commonsRepository.getUtilities().groupByColumnAndSumValues(stream, obj -> obj.getVlTotServ());
                List<Map.Entry<String, Double>> listEntries = this.commonsRepository.getUtilities().getValuesFromMap(getVlTotServ); //nao usar getVlTotServ.entrySet()
                listEntries.forEach(obj -> {
                	
                    viewTotalDetlh.get().setQtdTotalDeEstados(getUfs.size());
                    viewTotalDetlh.get().setQtdTotalDeCidades(getQtdCidades.get("CODIBGE").size());
               		viewTotalDetlh.get().setSomatoriaVlTotServ(obj.getValue());
                    viewTotalDetlh.get().setListaItemNfServs(listDto);
                    
                });
                HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
                return ResponseEntity.ok().headers(headers).body(viewTotalDetlh.get());
            }
            return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
        } catch (Exception ex) {
            return new ResponseEntity<>(commonsValidation.createResponseTemplate(String.format(ex.getMessage())),
                    HttpStatus.BAD_REQUEST);
        }
    }
    
    // ESTE ENDPOINT VAI ATENDER O PAINEL LATERAL ESQUERDO , o MAPA.
    // SENDO POSSIVEL DETALHAR AS CIDADES E QUANTIDADE EMITIDAS POR CIDADE CASO SELECIONADO.
    @ApiOperation(value = "Detalhar buscarPorEstadoDetalhado")
    @PostMapping("/buscarPorFiltros/vDashTotCidadeEstado")
    public ResponseEntity<Object> buscarPorEstadoDetalhado(@RequestParam(required = false) Map<String, String> requestParams,@RequestBody Filters filters) {

    	final NfServCommons nfServCommons = NfServCommons.builder().commonsRepository(this.commonsRepository).build();

    	AtomicLong qtdTotalTodosEstados = new AtomicLong(0);
    	AtomicLong qtdTotalNotasEmitidasPais = new AtomicLong(0);
    	AtomicLong qtdTotalNotasProcPais = new AtomicLong(0);
    	AtomicLong qtdTotalNotasPendPais = new AtomicLong(0);
    	AtomicLong qtdTotalNotasAutorizPais = new AtomicLong(0);
    	AtomicLong qtdTotalNotasCancelPais = new AtomicLong(0);
    	AtomicLong qtdTotalNotasEmitidasEstado = new AtomicLong(0);
    	AtomicLong qtdTotalNotasProcEstado = new AtomicLong(0);
    	AtomicLong qtdTotalNotasPendEstado = new AtomicLong(0);
    	AtomicLong qtdTotalNotasAutorizEstado = new AtomicLong(0);
    	AtomicLong qtdTotalNotasCancelEstado = new AtomicLong(0);

    	AtomicReference<String> codIbge = new AtomicReference<>();
    	List<DetalhamentoTotalPorCidade> listCidades = new LinkedList<>();
    	List<DetalhamentoTotalEstadoCidade> listDetalhTotalEstadoCidade = new LinkedList<>();

    	AtomicReference<DetalhamentoTotalPais> detalhTotalPais = new AtomicReference<>(new DetalhamentoTotalPais());
    	AtomicReference<List<DetalhamentoTotalPorEstado>> listDetalhTotalPorEstado
    	= new AtomicReference<>(new ArrayList<>());

    	AtomicReference<List<DetalhamentoTotalPorCidade>> listDetalhTotalPorCidade
    	= new AtomicReference<>(new ArrayList<>());
    	AtomicReference<Integer> qtdTotalDeCidades = new AtomicReference<Integer>(0);
    	AtomicReference<Integer> qtdTotalEmitidasPorCidade = new AtomicReference<Integer>(0);
    	CommonsValidation commonsValidation = this.createCommonsValidation();


    	try {

    		List<ViewTotalEstado> listDto = this.nfServService.vDashTotCidadeEstado(filters,
    				requestParams,
    				entityManagerFactory);
    		
    		// stream é fechado a cada iteração.
    		Stream<ViewTotalEstado> stream;

    		// para nao poluir a mesma request e vir com as cidades detalhadas,
    		// por padrao isDestalharPorCid = false; MP 10-07-2023
    		if (filters.getIsDestalharPorCid().get() == true)
    			
    			return mountCidade(nfServCommons, qtdTotalNotasEmitidasEstado, qtdTotalNotasProcEstado,
    					qtdTotalNotasPendEstado, qtdTotalNotasAutorizEstado, qtdTotalNotasCancelEstado, listCidades,
    					listDetalhTotalEstadoCidade, listDto);

    		if (filters.getIsDestalharPorEstado().get() == true)
    			
    			return mountEstado(qtdTotalTodosEstados, qtdTotalNotasEmitidasPais, qtdTotalNotasProcPais,
    					qtdTotalNotasPendPais, qtdTotalNotasAutorizPais, qtdTotalNotasCancelPais,
    					qtdTotalNotasEmitidasEstado, qtdTotalNotasProcEstado, qtdTotalNotasPendEstado,
    					qtdTotalNotasAutorizEstado, qtdTotalNotasCancelEstado, codIbge, detalhTotalPais,
    					listDetalhTotalPorEstado, qtdTotalDeCidades, qtdTotalEmitidasPorCidade, listDto);

    		stream = listDto.stream();

    		// reduce para realizar as somas parciais e a soma total.
    		qtdTotalNotasEmitidasEstado.set(stream.reduce(0L, (sum, v) -> {
    			qtdTotalNotasProcEstado.addAndGet(v.getQtdTotalNotasProc());
    			qtdTotalNotasPendEstado.addAndGet(v.getQtdTotalNotasPend());
    			qtdTotalNotasAutorizEstado.addAndGet(v.getQtdTotalNotasAutoriz());
    			qtdTotalNotasCancelEstado.addAndGet(v.getQtdTotalNotasCancel());
    			return sum + v.getQtdTotalNotasProc() + v.getQtdTotalNotasPend() + v.getQtdTotalNotasAutoriz();
    		}, Long::sum));
    		stream.close();

    		//nao usar mapData.entrySet()
    		Map<String, Long> mapData = this.commonsRepository.getUtilities().groupByAndSum(
    				stream, obj -> obj.getUf(), 
    				this.commonsRepository.getUtilities()
    				.getListOfColumns());
    		Set<Entry<String, Long>> entrySet = mapData.entrySet();
    	    		
    		for (Entry<String, Long> entry : entrySet)
    			mountDetalhTotalPorCidade(entry,listDto,listDetalhTotalPorCidade );

    		AtomicReference<DetalhamentoTotalPorEstado> obj = new AtomicReference<>(new DetalhamentoTotalPorEstado());

    		obj.get().setListDetalhTotalPorCidade(listDetalhTotalPorCidade);
    		obj.get().setQtdTotalNotasProcessadas(qtdTotalNotasProcEstado.longValue());
    		obj.get().setQtdTotalNotasPendencia(qtdTotalNotasPendEstado.longValue());
    		obj.get().setQtdTotalNotasAutorizadas(qtdTotalNotasAutorizEstado.longValue());
    		obj.get().setQtdTotalNotasCanceladas(qtdTotalNotasCancelEstado.longValue());
    		obj.get().setQtdTotalNotasEmitidas(qtdTotalNotasEmitidasEstado.longValue());
    		obj.get().setMultOrgCd(listDto.stream().findFirst().get().getMultOrgCd());

    		listDto.stream().close();

    		HttpHeaders headers = this.nfServCommonsController.httpHeaders(String.valueOf(qtdTotalNotasEmitidasEstado));
    		return ResponseEntity.ok().headers(headers).body(obj);
    	} catch (Exception ex) {
    		return new ResponseEntity<>(commonsValidation.createResponseTemplate(String.format(ex.getMessage())),
    				HttpStatus.BAD_REQUEST);
    	}
    }

    @ApiOperation(value = "Get NfServ by Estado")
    @PostMapping("/buscarPorEstadoCidade/{page}/{length}")
    public ResponseEntity<Object> buscarPorEstadoCidade(@RequestParam(required = false) Map<String, Object> requestParams,
                                                        @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody NfServDto entityDto) {

        try {
            Long count = nfServRepository.countByEstadoCidade(null, entityDto, entityManagerFactory);
            if (count > 0) {
                List<NfServ> list = nfServRepository.findByFilters(entityDto, entityManagerFactory, requestParams, page, length);
                HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
                return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(nfServMapper::toDto, list));
            }
            return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @ApiOperation(value = "Get NfServ by filters")
    @PostMapping("/buscarPorFiltros/{page}/{length}")
    public ResponseEntity<Object> buscarPorFiltros(@RequestParam(required = false) Map<String, Object> requestParams,
                                                   @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody NfServDto entityDto) {

        try {
            Long count = nfServRepository.count(null, entityDto, entityManagerFactory);
            if (count > 0) {
                List<NfServ> list = nfServRepository.findByFilters(entityDto, entityManagerFactory, requestParams, page, length);
                HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
                return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(nfServMapper::toDto, list));
            }
            return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
    
    private ResponseEntity<Object> mountCidade ( final NfServCommons nfServCommons, AtomicLong qtdTotalNotasEmitidasEstado,
    		AtomicLong qtdTotalNotasProcEstado, AtomicLong qtdTotalNotasPendEstado,
    		AtomicLong qtdTotalNotasAutorizEstado, AtomicLong qtdTotalNotasCancelEstado,
    		List<DetalhamentoTotalPorCidade> listCidades,
    		List<DetalhamentoTotalEstadoCidade> listDetalhTotalEstadoCidade, List<ViewTotalEstado> listDto) {
    	
    	Stream<ViewTotalEstado> stream;
    	
    	if (listDto.size() > 0) {
    		
    		AtomicInteger index = new AtomicInteger(0);
    		Map<String, List<ViewTotalEstado>> mapData = this.commonsRepository.getUtilities().groupByColumn(listDto,
    				obj -> obj.getViewCidadeEmpresa()
    				.getIbgeCidade());
    		
    		//for(Map.Entry<String,List<ViewTotalEstado>> e : mapData.entrySet()){
    		//	if (e.getKey().equals(listDto.stream().findFirst()
    		//			.get().getViewCidadeEmpresa()
    		//			.getIbgeCidade())) {
    		//		String tmp = e.getValue().toString();
    		//	}
    		//}
    		
    		mapData.forEach((codIbje, list) -> {

    			index.getAndIncrement();
    			Integer count = this.commonsRepository.getUtilities().getListSize(codIbje, mapData);

    			DetalhamentoTotalPorCidade detalhamentoCidade = DetalhamentoTotalPorCidade.builder()

    					.uf(list.stream().findFirst().map(v -> v.getViewCidadeEmpresa().getUf()).orElse(null))
    					.descr(list.stream().findFirst().map(v -> v.getViewCidadeEmpresa().getDescrCidade()).orElse(null).toUpperCase())
    					.dtEmissao(list.stream().findFirst().map(v -> v.getDtEmissao()).orElse(null))
    					.dmDbDestino(list.stream().findFirst().map(v -> v.getViewCidadeEmpresa().getDmDbDestino()).orElse(null))
    					.codIbgeCidade(codIbje)

						.qtdTotalNotasProcessadas(
								nfServCommons.sumCurrentValuesInList(list, obj -> obj.getQtdTotalNotasProc())
										+ qtdTotalNotasProcEstado.getAndAdd(0))
						.qtdTotalNotasPendencia(
								nfServCommons.sumCurrentValuesInList(list, obj -> obj.getQtdTotalNotasPend())
										+ qtdTotalNotasPendEstado.getAndAdd(0))
						.qtdTotalNotasAutorizadas(
								nfServCommons.sumCurrentValuesInList(list, obj -> obj.getQtdTotalNotasAutoriz())
										+ qtdTotalNotasAutorizEstado.getAndAdd(0))
						.qtdTotalNotasCanceladas(
								nfServCommons.sumCurrentValuesInList(list, obj -> obj.getQtdTotalNotasCancel())
										+ qtdTotalNotasCancelEstado.getAndAdd(0))
						
    					.qtdTotalNotasEmitidas(list.stream()
    							.mapToLong(v -> v.getQtdTotalNotasProc() + v.getQtdTotalNotasPend()
    							+ v.getQtdTotalNotasAutoriz()
    							+ v.getQtdTotalNotasCancel()).sum())
    					.build();

    			list.stream().close();
    			listCidades.add(detalhamentoCidade);
    		});

    		stream = listDto.stream();
    		
    		Long totalOfCity = mapData.keySet()
    				.stream().distinct()
    				.count();
    		
    		qtdTotalNotasEmitidasEstado.addAndGet(stream.collect(Collectors.reducing(0L,
    				v -> v.getQtdTotalNotasProc() + v.getQtdTotalNotasPend()
    				+ v.getQtdTotalNotasAutoriz() + v.getQtdTotalNotasCancel(), Long::sum)));

    		DetalhamentoTotalEstadoCidade obj = DetalhamentoTotalEstadoCidade.builder()
    				.qtdTotalCidades(totalOfCity.intValue())
    				.qtdTotalNotasProcessadas(nfServCommons.sumCurrentValuesInList(listDto, o -> o.getQtdTotalNotasProc()).longValue())
    				.qtdTotalNotasPendencia(nfServCommons.sumCurrentValuesInList(listDto, o -> o.getQtdTotalNotasPend()))
    				.qtdTotalNotasAutorizadas(nfServCommons.sumCurrentValuesInList(listDto, o -> o.getQtdTotalNotasAutoriz()))
    				.qtdTotalNotasCanceladas(nfServCommons.sumCurrentValuesInList(listDto, o -> o.getQtdTotalNotasCancel()))
    				.qtdTotalNotasEmitidas(qtdTotalNotasEmitidasEstado.longValue())
    				.build();
    		
    		listDetalhTotalEstadoCidade.add(obj);
    		obj.setListDetalhamentoTotalCidades(listCidades);
    		stream.close();
    	} else {
    		return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders("0")).body(EMPTY_MSG);
    	}
    	HttpHeaders headers = this.nfServCommonsController.httpHeaders(String.valueOf(qtdTotalNotasEmitidasEstado));
    	return ResponseEntity.ok().headers(headers).body(listDetalhTotalEstadoCidade);
    }
    
    private ResponseEntity<Object> mountEstado(AtomicLong qtdTotalTodosEstados, AtomicLong qtdTotalNotasEmitidasPais,
			AtomicLong qtdTotalNotasProcPais, AtomicLong qtdTotalNotasPendPais, AtomicLong qtdTotalNotasAutorizPais,
			AtomicLong qtdTotalNotasCancelPais, AtomicLong qtdTotalNotasEmitidasEstado,
			AtomicLong qtdTotalNotasProcEstado, AtomicLong qtdTotalNotasPendEstado,
			AtomicLong qtdTotalNotasAutorizEstado, AtomicLong qtdTotalNotasCancelEstado,
			AtomicReference<String> codIbge, AtomicReference<DetalhamentoTotalPais> detalhTotalPais,
			AtomicReference<List<DetalhamentoTotalPorEstado>> listDetalhTotalPorEstado,
			AtomicReference<Integer> qtdTotalDeCidades, AtomicReference<Integer> qtdTotalEmitidasPorCidade,
			List<ViewTotalEstado> listDto) {
    	
    	
		if (listDto.size() > 0) {

		    AtomicInteger index = new AtomicInteger(0);

		    Function<ViewTotalEstado, String> groupByUf = obj -> obj.getUf();
		    Map<String, List<ViewTotalEstado>> mapData = this.commonsRepository.getUtilities().groupByColumn(listDto, groupByUf);
		    Set<String> totalOfUf = mapData.keySet().stream()
		    		.map(String::toUpperCase)
		    		.collect(Collectors.toSet());

		    Set<Entry<String, List<ViewTotalEstado>>> entrySetByUf = mapData.entrySet();

		    mapData.forEach((siglaUf, lstCidades) -> {

		        ViewCidadeEmpresa o = lstCidades.stream().findFirst().get().getViewCidadeEmpresa();
		        codIbge.getAndSet(o.getIbgeCidade());

		        qtdTotalNotasEmitidasEstado.set(lstCidades.stream().reduce(0L, (sum, v) -> {
		            qtdTotalNotasProcEstado.getAndAdd(v.getQtdTotalNotasProc());
		            qtdTotalNotasPendEstado.getAndAdd(v.getQtdTotalNotasPend());
		            qtdTotalNotasAutorizEstado.getAndAdd(v.getQtdTotalNotasAutoriz());
		            qtdTotalNotasCancelEstado.getAndAdd(v.getQtdTotalNotasCancel());

		            for (Entry<String, List<ViewTotalEstado>> entry : entrySetByUf) {
		                ViewCidadeEmpresa obj = entry.getValue().stream()
		                        .findFirst().get()
		                        .getViewCidadeEmpresa();

		                //mapData vira com mais cidades precisa comparar com o que vira no entrySet
		                AtomicBoolean countIndex = new AtomicBoolean(
		                        obj.getIbgeCidade().equals(
		                                codIbge.get())
		                                ? true : false);
		                if (countIndex.get() == true) {
		                    index.getAndIncrement();
		                }
		            }
		            qtdTotalEmitidasPorCidade.getAndSet(index.get());
		            return sum + v.getQtdTotalNotasProc() + v.getQtdTotalNotasPend()
		                    + v.getQtdTotalNotasAutoriz() + v.getQtdTotalNotasCancel();
		        }, Long::sum));

		        lstCidades.stream().close();

		        // necessário Set<String>> para não duplicar na contagem a cidade
		        // e não trazer a qtd de cidades duplicadas por estado
		        Map<Object, Set<String>> countCidadesByUf = entrySetByUf.stream()
		        		.filter(obj -> obj.getValue()
		        				.stream()
		        				.findAny()
		        				.get()
		        				.getViewCidadeEmpresa().getUf()
		        				.equals(o.getUf()))
		        		.flatMap(obj -> obj.getValue().stream())
		        		.collect(Collectors.groupingByConcurrent(
		        				obj -> obj.getViewCidadeEmpresa().getUf(),
		        				Collectors.mapping(
		        						obj -> obj.getViewCidadeEmpresa()
		        						.getIbgeCidade(),
		        						Collectors.toSet())));
		        
		        // Map<String, List<String>> getCityesbyUfs								 = listDto.stream()
                // 		.collect(Collectors.groupingBy(
	            // 				ViewTotalItemNfServ::getUf))
	            // 		.entrySet().stream()
	            // 		.collect(Collectors.toMap(
	            // 				Map.Entry::getKey, v -> v.getValue().stream()
	            // 				.map(ViewTotalItemNfServ::getDescrCidade)
	            // 				.distinct()
	            // 				.collect(Collectors.toList())));

		        qtdTotalDeCidades.getAndSet(countCidadesByUf.entrySet().stream()
		                .collect(Collectors.summingInt(obj -> obj.getValue().size())));

		        DetalhamentoTotalPorEstado detalhTotalPorEstado = DetalhamentoTotalPorEstado.builder()
		                .uf(siglaUf)
		                .qtdTotalCidades(qtdTotalDeCidades.get())
		                .multOrgCd(lstCidades.stream().findFirst().get().getMultOrgCd())
		                .qtdTotalNotasProcessadas(qtdTotalNotasProcEstado.longValue())
		                .qtdTotalNotasAutorizadas(qtdTotalNotasAutorizEstado.longValue())
		                .qtdTotalNotasPendencia(qtdTotalNotasPendEstado.longValue())
		                .qtdTotalNotasCanceladas(qtdTotalNotasCancelEstado.longValue())
		                .qtdTotalNotasEmitidas(qtdTotalNotasEmitidasEstado.longValue()).build();

		        listDetalhTotalPorEstado.get().add(detalhTotalPorEstado);

		        qtdTotalNotasProcPais.updateAndGet(obj -> obj + qtdTotalNotasProcEstado.longValue());
		        qtdTotalNotasPendPais.updateAndGet(obj -> obj + qtdTotalNotasPendEstado.longValue());
		        qtdTotalNotasAutorizPais.updateAndGet(obj -> obj + qtdTotalNotasAutorizEstado.longValue());
		        qtdTotalNotasCancelPais.updateAndGet(obj -> obj + qtdTotalNotasCancelEstado.longValue());
		        qtdTotalNotasEmitidasPais.updateAndGet(obj -> obj + qtdTotalNotasEmitidasEstado.longValue());

		        detalhTotalPais.get().setQtdTotalNotasProcessadas(qtdTotalNotasProcPais.longValue());
		        detalhTotalPais.get().setQtdTotalNotasPendencia(qtdTotalNotasPendPais.longValue());
		        detalhTotalPais.get().setQtdTotalNotasAutorizadas(qtdTotalNotasAutorizPais.longValue());
		        detalhTotalPais.get().setQtdTotalNotasCanceladas(qtdTotalNotasCancelPais.longValue());
		        detalhTotalPais.get().setQtdTotalNotasEmitidas(qtdTotalNotasEmitidasPais.longValue());
		        detalhTotalPais.get().setListDetalhTotalPorEstado(listDetalhTotalPorEstado);

		        detalhTotalPais.get().setQtdTotalEstados(totalOfUf.size());

		        qtdTotalNotasProcEstado.getAndSet(0);
		        qtdTotalNotasPendEstado.getAndSet(0);
		        qtdTotalNotasAutorizEstado.getAndSet(0);
		        qtdTotalNotasCancelEstado.getAndSet(0);
		        qtdTotalNotasEmitidasEstado.getAndSet(0);

		        index.getAndSet(0);
		    });

		    HttpHeaders headers = this.nfServCommonsController.httpHeaders(String.valueOf(qtdTotalTodosEstados));
		    return ResponseEntity.ok().headers(headers).body(detalhTotalPais);
		} else {
		    return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(String.valueOf(qtdTotalNotasEmitidasEstado)))
		            .body(EMPTY_MSG);
		}
	}
    
    private void mountDetalhTotalPorCidade(Entry<String, Long> entry , List<ViewTotalEstado> listDto, AtomicReference<List<DetalhamentoTotalPorCidade>> listDetalhTotalPorCidade) {
    	Stream<ViewTotalEstado> stream;
    	stream = listDto.stream();
    	
    	List<ViewTotalEstado> list = stream

    			.filter(obj -> obj.getUf()
    					.equals(entry.getKey()))
    			.map(obj -> {
    				DetalhamentoTotalPorCidade entity = DetalhamentoTotalPorCidade.builder()
    						.uf(obj.getViewCidadeEmpresa().getUf())
    						.descr(obj.getViewCidadeEmpresa().getDescrCidade())
    						.dtEmissao(obj.getDtEmissao())
    						.qtdTotalNotasAutorizadas(obj.getQtdTotalNotasAutoriz())
    						.qtdTotalNotasPendencia(obj.getQtdTotalNotasPend())
    						.qtdTotalNotasProcessadas(obj.getQtdTotalNotasProc())
    						.qtdTotalNotasCanceladas(obj.getQtdTotalNotasCancel())
    						.qtdTotalNotasEmitidas(this.commonsRepository.getUtilities().sumTotalValues(
    								obj.getQtdTotalNotasAutoriz(),
    								obj.getQtdTotalNotasPend(),
    								obj.getQtdTotalNotasProc(),
    								obj.getQtdTotalNotasCancel()))
    						.build();
    				listDetalhTotalPorCidade.get().add(entity);
    				return new ViewTotalEstado();
    			}).collect(Collectors.toList());
    	stream.close();
    }

    private ResponseEntity<Object> mountEntity(NfServDto entityDto, Long id, Empresa empresa, boolean update)
            throws NfServException {
    	
    	  CommonsValidation commonsValidation = this.createCommonsValidation();

        try {
        	
            NfServ entity = nfServMapper.toEntity(entityDto);
            HttpHeaders headers = new HttpHeaders();
            entity.setId(update ? id : null);
            entity = nfServRepository.save(entity);
            headers.add("Location", "/find/" + entity.getId());
            return ResponseEntity.status(update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED).headers(headers)
					.body(nfServMapper.toDto(entity));
		} catch (NoResultException ex) {
	        return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new NfServException(ex.getMessage(), ex);
		}
	}

    private StatusDmStProcDto mountDto(List<Long> values) {
        StatusDmStProcDto resultDto = StatusDmStProcDto.builder()
                .qtdProcess(values.get(0))
                .qtdPendencia(values.get(1))
                .qtdAutorizada(values.get(2))
                .qtdCancelada(values.get(3))
                .build();
        return resultDto;
    }

    private CommonsValidation createCommonsValidation() {
        return CommonsValidation.builder()
                .commonsRepository(this.commonsRepository)
                .nfServRepository(this.nfServRepository)
                .nfServService(this.nfServService)
                .build();
    }
}