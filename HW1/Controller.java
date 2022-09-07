package com.example.cs421assignment1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class Controller {
    //All elements of the form
    @FXML
    private Button btnGenerate;
    @FXML
    private Button btnClear;
    @FXML
    private CheckBox cbDollar;
    @FXML
    private CheckBox cbDollarOther;
    @FXML
    private CheckBox cbPercent;
    @FXML
    private CheckBox cbPercentOther;
    @FXML
    private Label lbl1;
    @FXML
    private Label lbl2;
    @FXML
    private Label lbl3;
    @FXML
    private Label lbl4;
    @FXML
    private Label lbl5;
    @FXML
    private Label lbl6;
    @FXML
    private Label lbl7;
    @FXML
    private Label lbl8;
    @FXML
    private Label lbl9;
    @FXML
    private Label lbl10;
    @FXML
    private Label lblName;
    @FXML
    private Label lblAge;
    @FXML
    private Label lblAnnualCost;
    @FXML
    private Label lblDecision;
    @FXML
    private Label lblHeight;
    @FXML
    private Label lblPolicyRequest;
    @FXML
    private Label lblRiskFactor;
    @FXML
    private Label lblTotalPolicy;
    @FXML
    private Label lblWeight;
    @FXML
    private RadioButton rbOtherDiscount;
    @FXML
    private RadioButton rbPastCustomer;
    @FXML
    private TextField txtAge;
    @FXML
    private TextField txtDiscount;
    @FXML
    private TextField txtDiscountOther;
    @FXML
    private TextField txtHeight;
    @FXML
    private TextField txtPolicy;
    @FXML
    private TextField txtWeight;
    @FXML
    private TextField txtFN;
    @FXML
    private TextField txtMI;
    @FXML
    private TextField txtLN;
    @FXML
    private Label lblAgeCheck;
    @FXML
    private Label lblHeightCheck;
    @FXML
    private Label lblWeightCheck;
    @FXML
    private Label lblPolicyCheck;
    @FXML
    private Label lblFNCheck;
    @FXML
    private Label lblMICheck;
    @FXML
    private Label lblLNCheck;


    /*
        Method to handle when either the generate button is pressed or the clear button is pressed.
        If the generate button is selected, then it will check to make sure there was input provided
        as well as checking if the inputted numbers were within the correct range for said category.
        If each check is passed the method will display the quote next to the form.
        If the clear button is selected the method will clear everything on the screen to start fresh
        with a new form.
    */
    @FXML
     void buttonPress(ActionEvent event) {
        if(event.getSource() == btnGenerate){
            //assigning text input to variables
            double age = 0;
            double height = 0;
            double weight = 0;
            double policy = 0;
            String a = txtAge.getText();
            String h = txtHeight.getText();
            String w = txtWeight.getText();
            String p = txtPolicy.getText();
            String fn = txtFN.getText();
            String mi = txtMI.getText();
            String ln = txtLN.getText();

            //error messages set to invisible to start
            lblAgeCheck.setVisible(false);
            lblHeightCheck.setVisible(false);
            lblWeightCheck.setVisible(false);
            lblPolicyCheck.setVisible(false);
            lblFNCheck.setVisible(false);
            lblMICheck.setVisible(false);
            lblLNCheck.setVisible(false);

            //checking if input was provided for every category
            Boolean pass = true;
            if(a == ""){
                lblAgeCheck.setVisible(true);
                pass = false;
            }if(h == ""){
                lblHeightCheck.setVisible(true);
                pass = false;
            }if(w == ""){
                lblWeightCheck.setVisible(true);
                pass = false;
            }if(p == ""){
                lblPolicyCheck.setVisible(true);
                pass = false;
            }
            //if a name was provided, if so, if the name contained only english letters
            if(fn == "" || !fn.matches("[a-zA-Z]+")){
                lblFNCheck.setVisible(true);
                pass = false;
            }if(mi == "" || !mi.matches("[a-zA-Z]+")){
                lblMICheck.setVisible(true);
                pass = false;
            }if(ln == "" || !ln.matches("[a-zA-Z]+")) {
                lblLNCheck.setVisible(true);
                pass = false;
            }
            if(pass == true) { //if input was provided and passed checks
                age = Double.parseDouble(a);
                height = Double.parseDouble(h);
                weight = Double.parseDouble(w);
                policy = Double.parseDouble(p);
                double ap = 0;
                double percent = 0;
                double dollar = 0;
                double r = risk(age, height, weight);
                double totalPolicy = policy;

                //calculating annual cost depending on discount selected
                if (cbPercent.isSelected()) {
                    String per = txtDiscount.getText();
                    percent = (Double.parseDouble(per)) / 100;
                    double discount = policy * percent;
                    totalPolicy = policy - discount;
                    ap = annualPolicy(r, totalPolicy);
                } else if (cbDollar.isSelected()) {
                    String dol = txtDiscount.getText();
                    dollar = Double.parseDouble(dol);
                    totalPolicy = policy - dollar;
                    ap = (annualPolicy(r, totalPolicy));
                } else if (cbPercentOther.isSelected()) {
                    String per = txtDiscountOther.getText();
                    percent = (Double.parseDouble(per)) / 100;
                    double discount = policy * percent;
                    totalPolicy = policy - discount;
                    ap = annualPolicy(r, totalPolicy);
                } else if (cbDollarOther.isSelected()) {
                    String dol = txtDiscountOther.getText();
                    dollar = Double.parseDouble(dol);
                    totalPolicy = policy - dollar;
                    ap = (annualPolicy(r, totalPolicy));
                } else {
                    ap = annualPolicy(r, totalPolicy);
                }
                //adding in tax
                double tax = ap * .06;
                ap = ap + tax;

                //converting doubles to string for output
                String riskString = String.valueOf(r);
                String annualPolicy = String.valueOf(ap);
                String tp = String.valueOf(totalPolicy);

                //checking if input provided is within the correct range
                if (age > 110 || age < 18) {
                    lblAgeCheck.setVisible(true);
                    pass = false;
                }
                if (height < 48 || height > 84) {
                    lblHeightCheck.setVisible(true);
                    pass = false;
                }
                if (weight < 80 || weight > 400) {
                    lblWeightCheck.setVisible((true));
                    pass = false;
                }
                if (policy < 1000 || policy > 1000000) {
                    lblPolicyCheck.setVisible(true);
                    pass = false;
                }
                if (pass == true) {
                    //display quote labels for name, age, height, etc.
                    lbl1.setVisible(true);
                    lbl2.setVisible(true);
                    lbl3.setVisible(true);
                    lbl4.setVisible(true);
                    lbl5.setVisible(true);
                    lbl6.setVisible(true);
                    lbl7.setVisible(true);
                    lbl8.setVisible(true);
                    lbl9.setVisible(true);
                    lbl10.setVisible(true);

                    //display quote information
                    lblName.setText(fn + " " + mi + ". " + ln);
                    lblAge.setText(a + " years");
                    lblHeight.setText(h + " inches");
                    lblWeight.setText(w + " lbs");
                    lblPolicyRequest.setText("$" + p);
                    lblRiskFactor.setText(riskString);
                    lblTotalPolicy.setText("$" + tp);
                    lblAnnualCost.setText("$" + annualPolicy);

                    //deciding whether risk factor is safe or unsafe
                    String decision;
                    if (r <= 0) {
                        decision = "UNSAFE";
                    } else {
                        decision = "SAFE";
                    }
                    lblDecision.setText(decision);
                    if (decision == "UNSAFE") {
                        lblDecision.setTextFill(Color.RED);
                    } else {
                        lblDecision.setTextFill(Color.GREEN);
                    }
                }
            }
        }
        /*
        When the button "Clear" is selected it clears all input, errors, and text boxes from system as well as
        clears quote if there is a quote on screen
         */
        if(event.getSource() == btnClear){
            txtAge.clear();
            txtHeight.clear();
            txtWeight.clear();
            txtPolicy.clear();
            txtFN.clear();
            txtMI.clear();
            txtLN.clear();
            rbOtherDiscount.setSelected(false);
            rbPastCustomer.setSelected(false);
            rbOtherDiscount.setSelected(false);
            txtDiscountOther.setVisible(false);
            cbPercentOther.setVisible(false);
            cbDollarOther.setVisible(false);
            txtDiscountOther.clear();
            cbPercentOther.setSelected(false);
            cbDollarOther.setSelected(false);
            txtDiscount.setVisible(false);
            cbPercent.setVisible(false);
            cbDollar.setVisible(false);
            txtDiscount.clear();
            cbPercent.setSelected(false);
            cbDollar.setSelected(false);
            lbl1.setVisible(false);
            lbl2.setVisible(false);
            lbl3.setVisible(false);
            lbl4.setVisible(false);
            lbl5.setVisible(false);
            lbl6.setVisible(false);
            lbl7.setVisible(false);
            lbl8.setVisible(false);
            lbl9.setVisible(false);
            lbl10.setVisible(false);
            lblName.setText("");
            lblAge.setText("");
            lblHeight.setText("");
            lblWeight.setText("");
            lblPolicyRequest.setText("");
            lblRiskFactor.setText("");
            lblDecision.setText("");
            lblTotalPolicy.setText("");
            lblAnnualCost.setText("");
            lblAgeCheck.setVisible(false);
            lblHeightCheck.setVisible(false);
            lblWeightCheck.setVisible(false);
            lblPolicyCheck.setVisible(false);
            lblFNCheck.setVisible(false);
            lblMICheck.setVisible(false);
            lblLNCheck.setVisible(false);


        }
    }
    /*
        method for visual controls when selecting the multi-policy discount, only one type of discount,
        either flat dollar or percentage, is able to be selected and there must be a selection in order
        to type into the text box. If mp disocunt is selected then other discount is not able to be selected.
    */
    @FXML
    void MultiPolicyDiscount(ActionEvent event) {
        if(event.getSource() == rbPastCustomer){
            if(rbPastCustomer.isSelected()){
                txtDiscount.setVisible(true);
                txtDiscount.setDisable(true);
                cbPercent.setVisible(true);
                cbDollar.setVisible(true);
                rbOtherDiscount.setSelected(false);
                txtDiscountOther.setVisible(false);
                cbPercentOther.setVisible(false);
                cbDollarOther.setVisible(false);
                txtDiscountOther.clear();
                cbPercentOther.setSelected(false);
                cbDollarOther.setSelected(false);
            }else {
                txtDiscount.setVisible(false);
                cbPercent.setVisible(false);
                cbDollar.setVisible(false);
                txtDiscount.clear();
                cbPercent.setSelected(false);
                cbDollar.setSelected(false);
            }
        }
    }

    @FXML
    void MultiPolicyCheck(ActionEvent event){
        if(event.getSource() == cbPercent){
            cbDollar.setSelected(false);
            txtDiscount.setDisable(false);
            txtDiscount.clear();
        } else if(event.getSource() == cbDollar) {
            cbPercent.setSelected(false);
            txtDiscount.setDisable(false);
            txtDiscount.clear();
        }

        if(cbPercent.isSelected() == false && cbDollar.isSelected() == false){
            txtDiscount.setDisable(true);
        }
    }

    /*
        method for visual controls when selecting the other discount, only one type of discount,
        either flat dollar or percentage, is able to be selected and there must be a selection in order
        to type into the text box. If other discount is selected mp discount is not able to be selected.
    */
    @FXML
    void OtherDiscount(ActionEvent event) {
        if(event.getSource() == rbOtherDiscount){
            if(rbOtherDiscount.isSelected()){
                txtDiscountOther.setVisible(true);
                txtDiscountOther.setDisable(true);
                cbPercentOther.setVisible(true);
                cbDollarOther.setVisible(true);
                rbPastCustomer.setSelected(false);
                txtDiscount.setVisible(false);
                cbPercent.setVisible(false);
                cbDollar.setVisible(false);
                txtDiscount.clear();
                cbPercent.setSelected(false);
                cbDollar.setSelected(false);
            }else {
                txtDiscountOther.setVisible(false);
                cbPercentOther.setVisible(false);
                cbDollarOther.setVisible(false);
                txtDiscountOther.clear();
                cbPercentOther.setSelected(false);
                cbDollarOther.setSelected(false);
            }
        }
    }

    @FXML
    void OtherCheck(ActionEvent event){
        if(event.getSource() == cbPercentOther){
            cbDollarOther.setSelected(false);
            txtDiscountOther.setDisable(false);
            txtDiscountOther.clear();
        } else if(event.getSource() == cbDollarOther) {
            cbPercentOther.setSelected(false);
            txtDiscountOther.setDisable(false);
            txtDiscountOther.clear();
        }

        if(cbPercentOther.isSelected() == false && cbDollarOther.isSelected() == false){
            txtDiscountOther.setDisable(true);
        }
    }

    /*
        method to calculate the risk factor for the customer. Given age, height, and weight
        method will return the risk factor of the customer.
    */
    static double risk(double age, double height, double weight){
        double risk;
        double underSqrt = (height * height) + (age * weight);
        double riskFactor;
        risk = (age + (Math.sqrt(underSqrt))) / (weight - (4.01 * age)); //risk factor equation
        riskFactor = (double) Math.round(risk * 1000d) / 1000d; //round the risk factor to 3 decimals

        return riskFactor;
    }

    /*
        Method to calculate the annual cost of the policy based on the risk factor calculated above
        and the policy amount requested by the customer.
    */
    public static double annualPolicy(double riskFactor, double policy){
        double annualPolicy = 0;
        if(riskFactor >= 0 || riskFactor <= 10){
            annualPolicy = ((10.1 - riskFactor) * .1) * policy;
            annualPolicy = (double) Math.round(annualPolicy * 100d) / 100d;
        }else if (riskFactor > 10){
           while(riskFactor > 10){ //divide by 10 until only one digit is in front of decimal
               riskFactor = riskFactor / 10;
           }
            annualPolicy = ((10.1 - riskFactor) * .1) * policy;
            annualPolicy = (double) Math.round(annualPolicy * 100d) / 100d;
        }else if(riskFactor < 0 || riskFactor >= -10){
            annualPolicy = ((10.1 + riskFactor) * .1) * policy;
            annualPolicy = (double) Math.round(annualPolicy * 100d) / 100d;
        }else if(riskFactor < -10){
            while(riskFactor < -10){ //divide by 10 until only one digit is in front of decimal
                riskFactor = riskFactor / 10;
            }
            annualPolicy = ((10.1 + riskFactor) * .1) * policy;
            annualPolicy = (double) Math.round(annualPolicy * 100d) / 100d;
        }
        return annualPolicy;
    }
}