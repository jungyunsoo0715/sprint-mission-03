package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> saveAll(List<ReadStatus> readStatusList) {
        readStatusList.forEach(readStatus -> this.data.put(readStatus.getId(), readStatus));
        return readStatusList;
    }

    @Override
    public List<ReadStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID id) {
       this.data.remove(id);
    }
}
