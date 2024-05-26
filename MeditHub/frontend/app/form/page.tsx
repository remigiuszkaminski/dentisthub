'use client';
import React, { useState, useEffect } from 'react';
import * as Yup from 'yup';
import { useSession } from 'next-auth/react';
import { Session } from 'next-auth';




interface Patient {
    id: string;
    name: string;
    phoneNumber: string;
    email: string;
    dateOfBirth: string;
    orders: Order[];
}

interface Order {
    id: string;
    patientId: string;
    doctorName: string;
    date: string;
    time: string;
    description: string;
}

async function fetchPatients(): Promise<Patient[]> {
    return await fetch(`http://localhost:8080/api/users/getPatients`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    }).then(r => r.json());
}

async function addPatient(patient: Omit<Patient, 'id' | 'orders'>): Promise<Response> {
    return await fetch(`http://localhost:8080/api/users/addPatient`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(patient),
    });
}

async function addOrder(patientId: string, order: Omit<Order, 'id'>, session: Session): Promise<Response> {
    return await fetch(`http://localhost:8080/api/users/addOrder/${patientId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + session?.accessToken
        },
        body: JSON.stringify(order),
    });
}

async function deleteOrder(orderId: string, patientId: string, session: Session): Promise<Response> {
    return await fetch(`http://localhost:8080/api/users/deleteOrder/${orderId}/${patientId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + session?.accessToken
        },
    });
}


export default function PatientsPage({params}: { params: { slug: string } }) {

    const [patients, setPatients] = useState<Patient[]>([]);
    const [newPatient, setNewPatient] = useState<Omit<Patient, 'id' | 'orders'>>({
        name: '',
        phoneNumber: '',
        email: '',
        dateOfBirth: '',
    });
    const [selectedPatientId, setSelectedPatientId] = useState<string | null>(null);
    const [newOrder, setNewOrder] = useState<Omit<Order, 'id'>>({
        patientId: '',
        doctorName: '',
        date: '',
        time: '',
        description: '',
    });

    const [showAddPatientForm, setShowAddPatientForm] = useState(false);
    const [showAddOrderForm, setShowAddOrderForm] = useState(false);

    const { data: session } = useSession();
    const { slug } = params;

    useEffect(() => {
        async function fetchData() {
            try {
                const patients = await fetchPatients();
                setPatients(patients);
                console.log('Patients:', patients)
            } catch (error) {
                console.error('Error fetching patients:', error);
            }
        }

        fetchData().then(r => r);
    }, []);


    const handleAddPatient = async () => {
        try {
            await addPatient(newPatient);
            setPatients(await fetchPatients());
            setNewPatient({
                name: '',
                phoneNumber: '',
                email: '',
                dateOfBirth: '',
            });
        } catch (error) {
            console.error('Error adding patient:', error);
        }
    }

    const handleAddOrder = async () => {
        try {
            if(selectedPatientId) {
                await addOrder(selectedPatientId, {...newOrder, patientId: selectedPatientId}, session as Session);
                setPatients(await fetchPatients());
                setNewOrder({
                    patientId: '',
                    doctorName: '',
                    date: '',
                    time: '',
                    description: '',
                });
            }} catch (error) {
                console.error('Error adding order:', error);
            }
        }

    const handleDeleteOrder = async (orderId: string, patientId: string) => {
        try {
            await deleteOrder(orderId, patientId, session as Session);
            setPatients(await fetchPatients());
        } catch (error) {
            console.error('Error deleting order:', error);
        }
    }

    const validationSchema = Yup.object().shape({
        name: Yup.string().required('Name is required'),
        email: Yup.string().email('Invalid email').required('Email is required'),
        phoneNumber: Yup.string().matches(/^\d{3}-\d{3}-\d{3}$/, 'Invalid phone number format').required('Phone number is required'),
        dateOfBirth: Yup.string().matches(/^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\d{4}$/, 'Invalid date format').required('Date of birth is required'),
        doctorName: Yup.string().required('Doctor name is required').max(30, 'Doctor name must be at most 30 characters'),
        description: Yup.string().max(250, 'Description must be at most 250 characters'),
    })




        return (
        
             
            <div className="flex flex-col md:flex-row p-4 space-x-4">
                
            <div className="flex-1 mb-4 md:mb-0">
                <h1 className="text-2xl font-bold mb-4">Patients</h1>
                <button
                    onClick={() => setShowAddPatientForm(!showAddPatientForm)}
                    className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 mb-4"
                >
                    {showAddPatientForm ? 'Cancel' : 'Add Patient'}
                </button>
                {showAddPatientForm && (
                    <div className="mb-6">
                        <h2 className="text-xl font-semibold mb-2">Add Patient</h2>
                        <input
                            type="text"
                            placeholder="Name"
                            value={newPatient.name}
                            onChange={(e) => setNewPatient({ ...newPatient, name: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <input
                            type="text"
                            placeholder="Phone Number"
                            value={newPatient.phoneNumber}
                            onChange={(e) => setNewPatient({ ...newPatient, phoneNumber: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <input
                            type="text"
                            placeholder="Email"
                            value={newPatient.email}
                            onChange={(e) => setNewPatient({ ...newPatient, email: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <input
                            type="text"
                            placeholder="Date of Birth"
                            value={newPatient.dateOfBirth}
                            onChange={(e) => setNewPatient({ ...newPatient, dateOfBirth: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <button
                            onClick={handleAddPatient}
                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                        >
                            Add Patient
                        </button>
                    </div>
                )}
                <div className="h-96 overflow-y-scroll border border-gray-300 p-4 rounded">
                    <h2 className="text-xl font-semibold mb-2">Patients List</h2>
                    <ul>
                        {patients.map((patient) => (
                            <li
                                key={patient.id}
                                onClick={() => {
                                    setSelectedPatientId(patient.id);
                                    setShowAddOrderForm(true);
                                }}
                                className={`"cursor-pointer p-2 border-b border-gray-200 hover:bg-gray-100 ${selectedPatientId === patient.id ? 'bg-gray-200' : ''}`}
                            >
                                <h3 className="text-lg font-semibold">{patient.name}</h3>
                                <p>{patient.phoneNumber}</p>
                                <p>{patient.email}</p>
                                <p>{patient.dateOfBirth}</p>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
            <div className="flex-1">
                {showAddOrderForm && selectedPatientId && (
                    <div>
                        <h2 className="text-xl font-semibold mb-2">Add Order for Selected Patient</h2>
                        <input
                            type="text"
                            placeholder="Doctor Name"
                            value={newOrder.doctorName}
                            onChange={(e) => setNewOrder({ ...newOrder, doctorName: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <input
                            type="text"
                            placeholder="Date"
                            value={newOrder.date}
                            onChange={(e) => setNewOrder({ ...newOrder, date: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <input
                            type="text"
                            placeholder="Time"
                            value={newOrder.time}
                            onChange={(e) => setNewOrder({ ...newOrder, time: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <input
                            type="text"
                            placeholder="Description"
                            value={newOrder.description}
                            onChange={(e) => setNewOrder({ ...newOrder, description: e.target.value })}
                            className="block w-full mb-2 p-2 border border-gray-300 rounded"
                        />
                        <button
                            onClick={handleAddOrder}
                            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                        >
                            Add Order
                        </button>
                    </div>
                )}
                {selectedPatientId && (
                    <div className="mt-4 overflow-y-scroll border border-gray-300 p-4 rounded h-96">
                        <h2 className="text-xl font-semibold mb-2">Orders for Selected Patient</h2>
                        <ul>
                            {patients
                                .find(patient => patient.id === selectedPatientId)?.orders
                                .map(order => (
                                    <li key={order.id} className="p-2 border-b border-gray-200 flex justify-between items-center">
                                        <div>
                                            <h5 className="font-semibold">{order.doctorName}</h5>
                                            <p>{order.date}</p>
                                            <p>{order.time}</p>
                                            <p>{order.description}</p>
                                        </div>
                                        <button
                                            onClick={() => handleDeleteOrder(order.id, selectedPatientId)}
                                            className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
                                        >
                                            Delete
                                        </button>
                                    </li>
                                ))}
                        </ul>
                    </div>
                )}
            </div>
        </div>
        
    );
    }

