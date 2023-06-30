const getUserSettings = (req, res) => {
  const userSettingsItems = [
    { id: "immutableItem", label: "Immutable Item Demo", value: "Test Immutable Item" },
    { id: "mutableItem", label: "Mutable Item Demo (Single Update)", value: "Test Mutable Item (Single Update)", update: { description: "Test dynamically rendered description", endpoint: { method: "put", route: "/appSettings/userSettings" } } },
    { id: "mutableItem", label: "Mutable Item Demo (Form Update)", value: "Test Mutable Item (Form Update)", mutable: true },
    { id: "verifiedItem", label: "Verified Item Demo", value: "Verified", verification: { verified: true } },
    { id: "unverifiedItem", label: "Unverified Item Demo", value: "Unverified", verification: { verified: false } }
  ];

  res.status(200).json({ errCode: '0', payload: { userSettingsItems, update: { endpoint: { method: "put", route: "/appSettings/userSettings" } } } });
}

module.exports = { getUserSettings };