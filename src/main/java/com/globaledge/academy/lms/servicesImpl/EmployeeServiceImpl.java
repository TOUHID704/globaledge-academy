package com.globaledge.academy.lms.servicesImpl;

import com.globaledge.academy.lms.dto.EmployeeDto;
import com.globaledge.academy.lms.entity.Employee;
import com.globaledge.academy.lms.repository.EmployeeRepository;
import com.globaledge.academy.lms.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @Override
    public Employee createEmployee(EmployeeDto dto) {
        Employee employee = modelMapper.map(dto, Employee.class);
        return repository.save(employee);
    }
}
