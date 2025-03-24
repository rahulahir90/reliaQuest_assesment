package com.reliaquest.server.service;

import com.reliaquest.server.config.ServerConfiguration;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockEmployeeService {

    private final Faker faker;

    @Getter
    private final List<MockEmployee> mockEmployees;

    public Optional<MockEmployee> findById(@NonNull UUID uuid) {
        return mockEmployees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getId())
                        && mockEmployee.getId().equals(uuid))
                .findFirst();
    }

    public MockEmployee create(@NonNull CreateMockEmployeeInput input) {
        final var mockEmployee = MockEmployee.from(
                ServerConfiguration.EMAIL_TEMPLATE.formatted(
                        faker.twitter().userName().toLowerCase()),
                input);
        mockEmployees.add(mockEmployee);
        log.debug("Added employee: {}", mockEmployee);
        return mockEmployee;
    }

    public boolean delete(@NonNull DeleteMockEmployeeInput input) {
        final var mockEmployee = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getName())
                        && employee.getName().equalsIgnoreCase(input.getName()))
                .findFirst();
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return true;
        }

        return false;
    }

    public OptionalInt getHighestSalary() {
        return mockEmployees.stream().mapToInt(MockEmployee::getSalary).max();
    }

    public Optional<List<MockEmployee>> findByName(@NonNull String name) {
        log.debug("Search by Name: {}", name);
        return Optional.of(mockEmployees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getName())
                        && mockEmployee.getName().equals(name))
                .collect(Collectors.toList()));
    }

    public List<String> getTopTenHighestEarningEmployeeNameList() {
        return mockEmployees.stream()
                .sorted(Comparator.comparingInt(MockEmployee::getSalary).reversed())
                .map(MockEmployee::getName)
                .limit(10)
                .collect(Collectors.toList());
    }
}
