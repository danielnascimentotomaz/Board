package br.com.dio.service.block;



public interface BlockService {

     boolean block(Long cardId, String reason);
     boolean unblock(final String reason, final Long cardId);
}
