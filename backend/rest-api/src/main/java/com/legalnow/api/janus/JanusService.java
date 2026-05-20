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

    public record JanusAllocation(long roomId, String pin) {}

    public JanusAllocation allocateRooms(UUID consultationId) {
        long roomId = Math.abs(consultationId.getMostSignificantBits());
        String pin = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        log.info("Allocating Janus rooms for consultation {}, room_id: {}", consultationId, roomId);

        try {
            janusClient.createTextRoom(roomId, pin);
            janusClient.createVideoRoom(roomId, pin);
            return new JanusAllocation(roomId, pin);
        } catch (JanusRoomCreationException e) {
            log.error("Failed to allocate rooms for consultation {}", consultationId, e);
            throw e;
        }
    }
}
