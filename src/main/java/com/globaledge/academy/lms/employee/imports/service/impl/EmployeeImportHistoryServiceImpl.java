package com.globaledge.academy.lms.employee.imports.service.impl;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportHistory;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportHistoryDTO;
import com.globaledge.academy.lms.employee.imports.service.EmployeeImportHistoryService;
import com.globaledge.academy.lms.employee.repository.EmployeeImportHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeImportHistoryServiceImpl implements EmployeeImportHistoryService {

    private final EmployeeImportHistoryRepository historyRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EmployeeImportHistoryDTO> getAllImportHistory() {
        log.info("Fetching all import history records");
        return historyRepository.findAllByOrderByImportedAtDesc().stream()
                .map(h -> modelMapper.map(h, EmployeeImportHistoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeImportHistoryDTO getImportHistoryById(Long id) {
        log.info("Fetching import history with ID: {}", id);
        return historyRepository.findById(id)
                .map(h -> modelMapper.map(h, EmployeeImportHistoryDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Import history not found with ID: " + id));
    }

    @Override
    public Resource downloadLogFile(Long importId) {
        log.info("Request to download log file for import ID: {}", importId);
        EmployeeImportHistory history = historyRepository.findById(importId)
                .orElseThrow(() -> new ResourceNotFoundException("Import history not found with ID: " + importId));

        if (history.getLogFilePath() == null || history.getLogFilePath().isEmpty()) {
            throw new ResourceNotFoundException("No log file available for import ID: " + importId);
        }

        try {
            Path filePath = Paths.get(history.getLogFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                log.info("Log file found and is readable: {}", history.getLogFilePath());
                return resource;
            } else {
                throw new ResourceNotFoundException("Log file not found or cannot be read: " + history.getLogFilePath());
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Could not read log file path: " + e.getMessage());
        }
    }
}