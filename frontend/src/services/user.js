const isUser = (currentUser) => (
    currentUser.roles.some(role => ['user'].includes(role.authority))
);

const isAdmin = (currentUser) => (
    currentUser.roles.some(role => ['admin'].includes(role.authority))
);

export {
    isUser,
    isAdmin,
}