package vms.vmscurrentstate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import vms.vmscurrentstate.dto.MachineDTO;

public interface StateRepository extends MongoRepository<MachineDTO, Integer> {

}
