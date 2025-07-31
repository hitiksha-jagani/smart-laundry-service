// Author: Hitiksha Jagani
// Description: Complete profile page for delivery agent (without react-hook-form)

import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";

const CompleteDeliveryAgentProfilePage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [formValues, setFormValues] = useState({
        dateOfBirth: "",
        vehicleNumber: "",
        bankName: "",
        accountHolderName: "",
        bankAccountNumber: "",
        ifscCode: "",
        gender: "",
    });

    const [files, setFiles] = useState({
        aadharCard: null,
        panCard: null,
        drivingLicense: null,
        profilePhoto: null,
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormValues((prev) => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        const { name, files } = e.target;
        setFiles((prev) => ({ ...prev, [name]: files[0] }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
        const formData = new FormData();

        // JSON part
        formData.append("data", JSON.stringify(formValues));

        // File parts
        if (files.aadharCard) formData.append("aadharCard", files.aadharCard);
        if (files.panCard) formData.append("panCard", files.panCard);
        if (files.drivingLicense) formData.append("drivingLicense", files.drivingLicense);
        if (files.profilePhoto) formData.append("profilePhoto", files.profilePhoto);

        const response = await axios.post("http://localhost:8080/profile/complete", formData, {
            headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
        });

        alert(response.data);
        setFormValues({
            dateOfBirth: "",
            vehicleNumber: "",
            bankName: "",
            accountHolderName: "",
            bankAccountNumber: "",
            ifscCode: "",
            gender: "",
        });
        setFiles({
            aadharCard: null,
            panCard: null,
            drivingLicense: null,
            profilePhoto: null,
        });

        logout();
        navigate("/customer/dashboard");

        } catch (error) {
            console.error("Submit error:", error);
            alert(error.response?.data || "Something went wrong.");
        }
    };

    useEffect(() => {
        document.body.classList.add('delivery-agent-body');
        return () => {
          document.body.classList.remove('delivery-agent-body');
        };
    }, []);

    return (

        <div style={{ marginTop: '30px' }}>

            <h1 className="heading-agent h1-agent">COMPLETE PROFILE</h1>

            <div>
      
                <form onSubmit={handleSubmit} encType="multipart/form-data" >
                
                    <div className="agent-box" style={{ width: '100%', maxWidth: '700px', marginTop: '100px' }}>
                        
                        {/* ROW: Date of Birth + Vehicle Number */}
                        <div className="agent-grid-row">
                            <div className="agent-field">
                                <label>Date Of Birth *</label>
                                <input className="agent-input-field" type="date" name="dateOfBirth" value={formValues.dateOfBirth} onChange={handleInputChange} required />
                            </div>
                            <div className="agent-field">
                                <label>Vehicle Number *</label>
                                <input className="agent-input-field" type="text" name="vehicleNumber" value={formValues.vehicleNumber} onChange={handleInputChange} required />
                            </div>
                        </div>

                        {/* ROW: Bank Name + Account Holder Name */}
                        <div className="agent-grid-row">
                            <div className="agent-field">
                                <label>Bank Name *</label>
                                <input className="agent-input-field" type="text" name="bankName" value={formValues.bankName} onChange={handleInputChange} required />
                            </div>
                            <div className="agent-field">
                                <label>Account Holder Name *</label>
                                <input className="agent-input-field" type="text" name="accountHolderName" value={formValues.accountHolderName} onChange={handleInputChange} required />
                            </div>
                        </div>

                        {/* ROW: Bank Account Number + IFSC Code */}
                        <div className="agent-grid-row">
                            <div className="agent-field">
                                <label>Bank Account Number *</label>
                                <input className="agent-input-field" type="text" name="bankAccountNumber" value={formValues.bankAccountNumber} onChange={handleInputChange} required />
                            </div>
                            <div className="agent-field">
                                <label>IFSC Code *</label>
                                <input className="agent-input-field" type="text" name="ifscCode" value={formValues.ifscCode} onChange={handleInputChange} required />
                            </div>
                        </div>

                        {/* ROW: Gender + Aadhar Card */}
                        <div className="agent-grid-row">
                            <div className="agent-field">
                                <label>Gender *</label>
                                <select className="agent-input-field" name="gender" value={formValues.gender} onChange={handleInputChange} required>
                                <option value="">Select Gender</option>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                                </select>
                            </div>
                            <div className="agent-field">
                                <label>Aadhar Card *</label>
                                <input className="agent-input-field" type="file" name="aadharCard" accept="image/*,application/pdf" onChange={handleFileChange} required />
                            </div>
                        </div>

                        {/* ROW: PAN Card + Driving License */}
                        <div className="agent-grid-row">
                            <div className="agent-field">
                                <label>PAN Card</label>
                                <input className="agent-input-field" type="file" name="panCard" accept="image/*,application/pdf" onChange={handleFileChange} />
                            </div>
                            <div className="agent-field">
                                <label>Driving License *</label>
                                <input className="agent-input-field" type="file" name="drivingLicense" accept="image/*,application/pdf" onChange={handleFileChange} required />
                            </div>
                        </div>

                        {/* ROW: Profile Photo + empty (or another field later) */}
                        <div className="agent-grid-row">
                            <div className="agent-field">
                                <label>Profile Photo *</label>
                                <input className="agent-input-field" type="file" name="profilePhoto" accept="image/*" onChange={handleFileChange} required />
                            </div>
                        </div>
                        
                        <button type="submit" className="agent-btn">
                            SUBMIT
                        </button>
                    </div>

                </form>

            </div>

        </div>

    );
};

export default CompleteDeliveryAgentProfilePage;
