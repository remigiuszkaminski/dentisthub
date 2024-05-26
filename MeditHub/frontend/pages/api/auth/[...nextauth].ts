import NextAuth, { NextAuthOptions } from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import omit from 'lodash.omit';
import { User } from "next-auth";


export const authOptions: NextAuthOptions = {
    providers: [
        CredentialsProvider({
            name: 'Credentials',
            credentials: {
                username: { label: "username", type: "text" },
                password: { label: "password", type: "password" }
            },
            async authorize(credentials) {
                const res = await fetch("http://localhost:8080/api/users/login", {
                    method: 'POST',
                    body: JSON.stringify(credentials),
                    headers: { "Content-Type": "application/json" }
                });
                const user = await res.json();

                if (res.ok && user) {
                    return user;
                }
                return null;
            }
        })
    ],
    session: {
        strategy: 'jwt',
    },
    callbacks: {
        async jwt({ token, user }) {
            if (user && 'user' in user) {
                const userWithoutSensitiveData = omit(user.user, ['password', 'email']);
                return {
                    ...token,
                    userData: userWithoutSensitiveData,
                    accessToken: user.accessToken
                };
            }
            return token;
        },

        async session({ session, token }) {
            if (token?.userData) {
                // @ts-ignore
                session.user = token.userData as User;
                session.accessToken = token.accessToken as string;
            }
            return session;
        }
    },
    pages: {
        signIn: '/auth/login',
    }
};

export default NextAuth(authOptions);
