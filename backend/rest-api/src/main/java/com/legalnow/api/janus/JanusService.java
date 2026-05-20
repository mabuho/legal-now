package com.legalnow.api.janus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JanusService {
    private final JanusClient janusClient;

    public long allocateRooms(UUID consultationId) {
        long roomId = Math.abs(consultationId.getMostSignificantBits());
        log.info("Allocating Janus rooms for consultation {}, room_id: {}", consultationId, roomId);

        try {
            janusClient.createTextRoom(roomId);
            janusClient.createVideoRoom(roomId);
            return roomId;
        } catch (JanusRoomCreationException e) {
            log.error("Failed to allocate rooms for consultation {}", consultationId, e);
            throw e;
        }
    }
}
