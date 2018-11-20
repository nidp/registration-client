package io.mosip.registration.controller;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.RegistrationApprovalDTO;
import io.mosip.registration.entity.GlobalContextParam;
import io.mosip.registration.service.GlobalContextParamService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

/**
 *{@code OnHoldController} is controller class for onHoldComment fxml page 
 *which consists the methods for updating the status to onhold
 *
 * @author Mahesh Kumar
 */
@Controller
public class OnHoldController extends BaseController implements Initializable{

	/**
	 * Stage
	 */
	private Stage primarystage;
			
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(OnHoldController.class);

	@Autowired
	private GlobalContextParamService globalContextParamService;

	/**
	 * Combobox for for on hold reason
	 */
	@FXML
	private ComboBox<String> onHoldComboBox;
	/**
	 * Button for Submit
	 */
	@FXML
	private Button submit;

	@FXML
	private Hyperlink exit;


	private List<Map<String, String>> onHoldMapList;

	private RegistrationApprovalDTO onHoldRegData;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.fxml.Initializable#initialize(java.net.URL,
	 * java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.debug("REGISTRATION - PAGE_LOADING - REGISTRATION_ONHOLD_CONTROLLER", APPLICATION_NAME, APPLICATION_ID,
				"Page loading has been started");
		GlobalContextParam globalContextParam = globalContextParamService
				.findRejectionOnholdComments("ONHOLD_COMMENTS");
		submit.disableProperty().set(true);
		onHoldComboBox.getItems().clear();
		onHoldComboBox.setItems(FXCollections.observableArrayList(globalContextParam.getVal().split(",")));

	}

	/**
	 * Method to get the Stage and Registration Id from the other controller page
	 * 
	 * @param id
	 * @param stage
	 */
	public void initData(RegistrationApprovalDTO regData, Stage stage, List<Map<String, String>> mapList) {
		onHoldRegData = regData;
		primarystage = stage;
		onHoldMapList = mapList;
	}

	/**
	 * {@code updatePacketStatus} is event class for updating packet status to
	 * onhold
	 * 
	 * @param event
	 */
	public void updatePacketStatus() {
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_ONHOLD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Packet updation as on hold has been started");
		
		for (Map<String, String> registrationMap : onHoldMapList) {
			if (registrationMap.containsValue(onHoldRegData.getId())) {
				onHoldMapList.remove(registrationMap);
			}
		}
		
		Map<String, String> map = new HashMap<>();
		map.put("registrationID", onHoldRegData.getId());
		map.put("statusCode", RegistrationClientStatusCode.ON_HOLD.getCode());
		map.put("statusComment", onHoldComboBox.getSelectionModel().getSelectedItem());
		onHoldMapList.add(map);

		generateAlert(RegistrationConstants.STATUS, AlertType.INFORMATION, RegistrationConstants.ONHOLD_STATUS_MESSAGE);
		submit.disableProperty().set(true);
		primarystage.close();
		LOGGER.debug("REGISTRATION - UPDATE_PACKET_STATUS - REGISTRATION_ONHOLD_CONTROLLER", APPLICATION_NAME,
				APPLICATION_ID, "Packet updation as on hold has been ended");
	}

	/**
	 * {@code rejectionWindowExit} is event class to exit from reason for rejection
	 * pop up window.
	 * 
	 * @param event
	 */
	public void exitWindow() {
		primarystage.close();

	}

	/**
	 * Rejection combobox action.
	 * 
	 * @param event
	 */
	public void onHoldComboboxAction() {
		submit.disableProperty().set(false);
	}
}