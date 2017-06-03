package org.onem2m.cse.service;

import org.onem2m.cse.domain.HomeStatus;
import org.onem2m.cse.repository.HomeStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeStatusService {

	@Autowired
	HomeStatusRepository homeStatusRepo;
	
	public HomeStatus findOne(String id) {
		return homeStatusRepo.findOne(id);
	}
	
	public HomeStatus update(HomeStatus homeStatus) {
		return homeStatusRepo.save(homeStatus);
	}
}
