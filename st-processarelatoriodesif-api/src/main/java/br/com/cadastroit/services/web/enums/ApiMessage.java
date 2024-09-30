package br.com.cadastroit.services.web.enums;
public enum ApiMessage {

    RECORD_CREATED("Registro %s[ID] %s criado com sucesso"),
    RECORD_UPDATED("Registro %s[ID] %s atualizado com sucesso!"),
    RECORD_DELETED("Registro %s[ID] %s removido com sucesso!"),
    RECORD_NOT_FOUND_ID("Registro nao encontrado para %s[ID] = %s"),
    INTERNAL_SERVER_ERROR("Erro durante o processamento da requisicao. [Erro] = %s"),
    INVALID_CREDENTIALS("Credencial invalida, multorg[%s]"),
    INVALID_PARSER_JSON("Erro de conversao JSON. [Erro] = [%s]"),
    INVALID_HEADER("Erro na composicao do header. Acesse o swagger e verifique os atributos / parametros que devem ser encaminhados na requisicao"),
    COLLECTION_IS_EMPTY("Nenhum registro devolvido para pesquisa");


    private String message;
    public String message(){
        return this.message;
    }
    
    ApiMessage(String message){
        this.message = message;
    }
    
    // ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiMessage.COLLECTION_IS_EMPTY.message()) : null;
    // return collection.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiMessage.COLLECTION_IS_EMPTY.message()) :
    //        ResponseEntity.status(HttpStatus.OK).body(collection);
}