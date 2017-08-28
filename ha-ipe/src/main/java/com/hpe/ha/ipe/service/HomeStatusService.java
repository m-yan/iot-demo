package com.hpe.ha.ipe.service;

import com.hpe.ha.ipe.domain.HomeStatus;
import com.hpe.ha.ipe.repository.HomeStatusRepository;
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
