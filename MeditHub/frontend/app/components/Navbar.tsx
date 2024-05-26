// components/Navbar.tsx
'use client'
import Link from 'next/link';
import {signIn, signOut, useSession} from "next-auth/react";


export default function Navbar() {
    const {data: session } = useSession();
    return (
        <div className="flex justify-between items-center bg-gray-800 pb-2.5 pr-2.5 pl-2.5">
            <div className="flex items-center">
                <Link href="/">
                    <button className="bg-gray-700 text-white px-3 h-8 mt-2 rounded-md text-sm font-medium mr-3">
                        Home
                    </button>
                </Link>

                <Link href={'/test_page'}>
                    <button className="bg-gray-700 text-white px-3 h-8 mt-2 rounded-md text-sm font-medium mr-3">
                        Any Page
                    </button>
                </Link>
                {session ? (
                <Link href={'/form'}>
                    <button className="bg-gray-700 text-white px-3 h-8 mt-2 rounded-md text-sm font-medium mr-3">
                        Patients
                    </button>
                </Link>
                ) : (<div></div>)}
            </div>

            <div>
                <p></p>
            </div>


            <div className="flex items-center">
                {session ? (
                    <div className="flex items-center">
                        <div className="flex items-center">
                            <p className="text-white text-sm font-bold mt-3 mr-6">Welcome, {session.user?.username}!</p>
                            <Link href={`/profile/${session.user?.id}`}>
                                <button className="bg-gray-700 text-white px-3 h-8 mt-2 rounded-md text-sm font-medium mr-3">
                                    Profile
                                </button>
                            </Link>
                        </div>
                    <button className="bg-gray-700 text-white px-3 h-8 mt-2 rounded-md text-sm font-medium mr-3"
                            onClick={() => signOut()}>
                        Logout
                    </button>
                    </div>
                ) : (

                    <button className="bg-gray-700 text-white px-3 h-8 mt-2 rounded-md text-sm font-medium"
                            onClick={() => signIn()}>
                        Login
                    </button>
                )}

            </div>

        </div>
    )
}

