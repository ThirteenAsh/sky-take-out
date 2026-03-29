package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    PageResult pageQuary(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, long id);

    Employee getById(Long id);

    void update(EmployeeDTO employeeDTO);
}
