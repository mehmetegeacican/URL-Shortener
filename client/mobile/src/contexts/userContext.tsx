import { createContext, ReactNode, useContext, useEffect, useReducer } from "react";
import AsyncStorage from '@react-native-async-storage/async-storage';

// 1. User State shape
export interface UserState {
    username: string;
    token: string;
    userId: string;
    isAuthenticated: boolean;
    isAdmin:boolean;
}

// 2. User Actions
export type UserAction =
    { type: 'LOGIN'; payload: { username: string; token: string, userId: string, admin:boolean } }
    | { type: 'LOGOUT' }
    | { type: 'UPDATE_PROFILE'; payload: Partial<UserState> };


// 3. User Context Types
export interface UserContextType {
    state: UserState;
    dispatch: React.Dispatch<UserAction>;
}
// 4. Context Creations
const UserContext = createContext<UserContextType | null>(null);

// 5. Initial States
const initialState: UserState = {
    username: '',
    token: '',
    userId: '',
    isAuthenticated: false,
    isAdmin:false
};

// 6. Reducer , strict typing
function userReducer(state: UserState, action: UserAction): UserState {
    switch (action.type) {
        case 'LOGIN':

            return {
                ...state,
                username: action.payload.username,
                userId: action.payload.userId,
                token: action.payload.token,
                isAuthenticated: true,
                isAdmin: action.payload.admin
            };
        case 'LOGOUT':
            return initialState;
        case 'UPDATE_PROFILE':
            return {
                ...state,
                ...action.payload,
            };
        default:
            // Exhaustiveness check for TypeScript
            const exhaustiveCheck: never = action;
            throw new Error(`Unhandled action: ${exhaustiveCheck}`);
    }
}

// 7. Provider Props type
interface UserProviderProps {
    children: ReactNode;
}

// 8. Provider Component
export function UserProvider({ children }: UserProviderProps) {
    const [state, dispatch] = useReducer(userReducer, initialState);


    useEffect(() => {
        async function loadStoredUser() {
            try {
                const storedUser = await AsyncStorage.getItem('user');
                if (storedUser) {
                    const userData = JSON.parse(storedUser);
                    dispatch({ type: 'LOGIN', payload: userData });
                }
            } catch (error) {
                console.error('Failed to load user session', error);
            }
        }
        loadStoredUser();
    }, []);

    useEffect(() => {
        console.log(state);
    }, [state]);

    return (
        <UserContext.Provider value={{ state, dispatch }}>
            {children}
        </UserContext.Provider>
    );
}

// 9. Custom Hook with built-in safety check
export function useUserContext(): UserContextType {
    const context = useContext(UserContext);

    if (context === null) {
        throw new Error('useUser must be used within a UserProvider');
    }

    return context;
}