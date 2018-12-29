package vending.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import vending.dto.MachineDTO;

public interface StateRepository extends MongoRepository<MachineDTO, Integer> {

}
