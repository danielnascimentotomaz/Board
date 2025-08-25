package br.com.dio.utils;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class OffSetDateTimeConverter {

        //private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;

        // Horário de Brasília (BRT, sem horário de verão)
        private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.of("-03:00");

        public static OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
            if (timestamp == null) {
                return null;
            }
            return timestamp.toInstant().atOffset(DEFAULT_OFFSET);
        }

}
