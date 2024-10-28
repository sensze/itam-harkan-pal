package com.ifkusyoba.itam_harkan_pal.features.timesheet.service;

import com.ifkusyoba.itam_harkan_pal.core.exception.DataNotFoundException;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.dto.job.GetJobResponse;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.dto.workorder.PostWorkOrderRequest;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.dto.workorder.PatchWorkOrderDurationRequest;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.dto.workorder.GetWorkOrderResponse;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.dto.workorder.PutWorkOrderRequest;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.entity.Job;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.entity.Timesheet;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.entity.WorkOrder;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.repository.TimesheetRepository;
import com.ifkusyoba.itam_harkan_pal.features.timesheet.repository.WorkOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

@Service
public class WorkOrderService {
        private final WorkOrderRepository workOrderRepository;
        private final TimesheetRepository timesheetRepository;

        @Autowired
        public WorkOrderService(WorkOrderRepository workOrderRepository, TimesheetRepository timesheetRepository) {
                this.workOrderRepository = workOrderRepository;
                this.timesheetRepository = timesheetRepository;
        }

        public List<GetWorkOrderResponse> getAllWorkOrder() {
                List<WorkOrder> workOrders = workOrderRepository.findAll();
                return workOrders.stream()
                                .map(workOrder -> GetWorkOrderResponse.builder()
                                                .idWorkOrder(workOrder.getIdWorkOrder())
                                                .workOrderCode(workOrder.getWorkOrderCode())
                                                .workOrderName(workOrder.getWorkOrderName())
                                                .workOrderDuration(workOrder.getWorkOrderDuration())
                                                .timesheetId(workOrder.getTimesheet() != null
                                                                ? workOrder.getTimesheet().getIdTimesheet()
                                                                : null)
                                                .build())
                                .collect(Collectors.toList());
        }

        public List<GetWorkOrderResponse> getWorkOrderByTimesheetId(Integer timesheetId) {
                List<WorkOrder> workOrders = workOrderRepository.findByTimesheetId(timesheetId)
                                .orElseThrow(() -> new DataNotFoundException(
                                                "No WorkOrders found for Timesheet with id " + timesheetId));

                return workOrders.stream()
                                .map(workOrder -> GetWorkOrderResponse.builder()
                                                .idWorkOrder(workOrder.getIdWorkOrder())
                                                .workOrderCode(workOrder.getWorkOrderCode())
                                                .workOrderName(workOrder.getWorkOrderName())
                                                .workOrderDuration(workOrder.getWorkOrderDuration())
                                                .timesheetId(workOrder.getTimesheet() != null
                                                                ? workOrder.getTimesheet().getIdTimesheet()
                                                                : null)
                                                .build())
                                .collect(Collectors.toList());
        }

        public void deleteWorkOrder(Integer id) {
                try {
                        WorkOrder workOrder = workOrderRepository.findById(id)
                                        .orElseThrow(() -> new DataNotFoundException(
                                                        "WorkOrder with id " + id + " not found"));
                        workOrderRepository.delete(workOrder);
                } catch (Exception e) {
                        throw new RuntimeErrorException(new Error("Failed to delete WorkOrder with id " + id));
                }
        }

        public GetWorkOrderResponse getWorkOrderById(Integer id) {
                WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
                if (workOrder == null) {
                        throw new DataNotFoundException("WorkOrder with id " + id + " not found");
                }
                return GetWorkOrderResponse.builder()
                                .idWorkOrder(workOrder.getIdWorkOrder())
                                .workOrderCode(workOrder.getWorkOrderCode())
                                .workOrderName(workOrder.getWorkOrderName())
                                .workOrderDuration(workOrder.getWorkOrderDuration())
                                .timesheetId(workOrder.getTimesheet() != null
                                                ? workOrder.getTimesheet().getIdTimesheet()
                                                : null)
                                .build();
        }

        @Transactional
        public GetWorkOrderResponse createWorkOrder(PostWorkOrderRequest request) {
                Timesheet timesheet = timesheetRepository.findById(request.getTimesheetId())
                                .orElseThrow(() -> new DataNotFoundException(
                                                "Timesheet with id " + request.getTimesheetId() + " not found"));
                WorkOrder workOrder = new WorkOrder();
                workOrder.setWorkOrderCode(request.getWorkOrderCode());
                workOrder.setWorkOrderName(request.getWorkOrderName());
                workOrder.setWorkOrderDuration(0);
                workOrder.setTimesheet(timesheet);
                workOrderRepository.save(workOrder);
                return GetWorkOrderResponse.builder()
                                .idWorkOrder(workOrder.getIdWorkOrder())
                                .workOrderCode(workOrder.getWorkOrderCode())
                                .workOrderName(workOrder.getWorkOrderName())
                                .workOrderDuration(workOrder.getWorkOrderDuration())
                                .timesheetId(workOrder.getTimesheet().getIdTimesheet())
                                .build();
        }

        @Transactional
        public GetWorkOrderResponse updateWorkOrder(Integer id, PutWorkOrderRequest request) {
                WorkOrder workOrder = workOrderRepository.findById(id)
                                .orElseThrow(() -> new DataNotFoundException("WorkOrder with id " + id + " not found"));

                workOrder.setWorkOrderCode(request.getWorkOrderCode());
                workOrder.setWorkOrderName(request.getWorkOrderName());
                workOrder.setWorkOrderDuration(request.getWorkOrderDuration());
                workOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                return GetWorkOrderResponse.builder()
                                .idWorkOrder(workOrder.getIdWorkOrder())
                                .workOrderCode(workOrder.getWorkOrderCode())
                                .workOrderName(workOrder.getWorkOrderName())
                                .workOrderDuration(workOrder.getWorkOrderDuration())
                                .timesheetId(workOrder.getTimesheet() != null
                                                ? workOrder.getTimesheet().getIdTimesheet()
                                                : null)
                                .build();
        }

        @Transactional
        public GetWorkOrderResponse updateWorkOrderDuration(Integer id, PatchWorkOrderDurationRequest request) {
                WorkOrder workOrder = workOrderRepository.findById(id).orElse(null);
                if (workOrder == null) {
                        throw new DataNotFoundException("WorkOrder with id " + id + " not found");
                }
                workOrder.setWorkOrderDuration(request.getWorkOrderDuration());
                workOrder.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                workOrderRepository.save(workOrder);
                return GetWorkOrderResponse.builder()
                                .idWorkOrder(workOrder.getIdWorkOrder())
                                .workOrderCode(workOrder.getWorkOrderCode())
                                .workOrderName(workOrder.getWorkOrderName())
                                .workOrderDuration(workOrder.getWorkOrderDuration())
                                .timesheetId(workOrder.getTimesheet() != null
                                                ? workOrder.getTimesheet().getIdTimesheet()
                                                : null)
                                .build();
        }

}
