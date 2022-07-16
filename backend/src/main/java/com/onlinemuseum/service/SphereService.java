package com.onlinemuseum.service;

import com.onlinemuseum.domain.entity.*;
import com.onlinemuseum.dto.*;
import com.onlinemuseum.mapper.*;
import com.onlinemuseum.repository.*;
import com.onlinemuseum.response.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class SphereService {
    private final SphereRepository sphereRepository;
    private final GlobalMapper globalMapper;

    private final Logger logger = LoggerFactory.getLogger(SphereService.class);

    @Autowired
    public SphereService(SphereRepository sphereRepository, GlobalMapper globalMapper) {
        this.sphereRepository = sphereRepository;
        this.globalMapper = globalMapper;
    }

    public SphereResponse createSphere(String name) {
        SphereResponse sphereResponse = new SphereResponse();
        List<String> failureMessages = new ArrayList<>();

        if (sphereRepository.existsByName(name)) {
            logger.info("A sphere with name \"{}\" already exists.", name);
            failureMessages.add(String.format("A sphere with name \"%s\" already exists.", name));

            sphereResponse.setFailureMessages(failureMessages);

            return sphereResponse;
        }

        Sphere sphereToSave = new Sphere();
        sphereToSave.setName(name);

        Sphere savedSphere = sphereRepository.save(sphereToSave);

        sphereResponse.setSphereDto(globalMapper.map(savedSphere, SphereDto.class));

        logger.info("A sphere with name \"{}\" was saved successfully.", name);

        return sphereResponse;
    }

    public List<SphereDto> getAllSpheres() {
        List<Sphere> spheres = sphereRepository.findAllByEnabledIsTrue();

        return globalMapper.mapList(spheres, SphereDto.class);
    }

    public SphereResponse updateSphere(Long id, SphereUpdateDto sphereUpdateDto) {
        SphereResponse sphereResponse = new SphereResponse();
        Optional<Sphere> sphereToUpdate = sphereRepository.findById(id);
        List<String> failureMessages = new ArrayList<>();

        if (sphereToUpdate.isEmpty()) {
            logger.info("A sphere with id {} doesn't exist.", id);
            failureMessages.add(String.format("A sphere with id %d doesn't exist.", id));

            sphereResponse.setFailureMessages(failureMessages);

            return sphereResponse;
        }

        String sphereName = sphereUpdateDto.getName();

        if (sphereRepository.existsByName(sphereName)) {
            logger.info("A sphere with name \"{}\" exists.", sphereUpdateDto.getName());

            failureMessages.add(String.format("A sphere with name \"%s\" already exists.", sphereName));
        }

        if (!failureMessages.isEmpty()) {
            sphereResponse.setFailureMessages(failureMessages);

            return sphereResponse;
        }

        sphereToUpdate.get().setName(sphereUpdateDto.getName());
        Sphere updatedSphere = sphereRepository.save(sphereToUpdate.get());

        sphereResponse.setSphereDto(globalMapper.map(updatedSphere, SphereDto.class));

        logger.info("The sphere with id {} was updated successfully.", id);

        return sphereResponse;
    }

    public SphereResponse hideSphere(Long id) {
        SphereResponse sphereResponse = new SphereResponse();
        Optional<Sphere> sphereToHide = sphereRepository.findById(id);
        List<String> failureMessages = new ArrayList<>();

        if (sphereToHide.isEmpty()) {
            logger.info("A sphere with id {} doesn't exist.", id);
            failureMessages.add(String.format("A sphere with id %d doesn't exist.", id));

            sphereResponse.setFailureMessages(failureMessages);

            return sphereResponse;
        }

        if (!sphereToHide.get().getEnabled()) {
            logger.info("A sphere with id {} was already hidden.", id);
            failureMessages.add(String.format("A sphere with id %d was already hidden.", id));

            sphereResponse.setFailureMessages(failureMessages);

            return sphereResponse;
        }

        sphereToHide.get().setEnabled(false);
        Sphere hiddenSphere = sphereRepository.save(sphereToHide.get());

        sphereResponse.setSphereDto(globalMapper.map(hiddenSphere, SphereDto.class));

        logger.info("The sphere with id {} was successfully hidden.", id);

        return sphereResponse;
    }
}
