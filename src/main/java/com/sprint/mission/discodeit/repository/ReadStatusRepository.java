package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    List<ReadStatus> saveAll(List<ReadStatus> readStatusList);
    List<ReadStatus> findAll();
    void deleteById(UUID id);
}
