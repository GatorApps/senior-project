const getAppAlert = (req, res) => {
  const reqApp = req.reqApp;

  // Example alert
  //   "appAlert": {
  //     "maintenanceMode": false,
  //     "severity": "info",
  //     "title": "Scheduled Maintenance",
  //     "message": "GatorApps is scheduled for necessary maintenance on Saturday, September 2nd, 2023 from 2AM to 6AM EST. Apps will have limited availability. We are always working hard for your better experience!",
  //     "actions": []
  //   }

  if (!reqApp?.alert?.displayAlert) return res.status(200).json({ errCode: '0', payload: { appAlert: { displayAlert: false } } });
  return res.status(200).json({ errCode: '0', payload: { appAlert: reqApp.alert } });
}

const getAppInfo = (req, res) => {
  const reqApp = JSON.parse(JSON.stringify(req.reqApp));

  // Example alert
  //   "appAlert": {
  //     "maintenanceMode": false,
  //     "severity": "info",
  //     "title": "Scheduled Maintenance",
  //     "message": "GatorApps is scheduled for necessary maintenance on Saturday, September 2nd, 2023 from 2AM to 6AM EST. Apps will have limited availability. We are always working hard for your better experience!",
  //     "actions": []
  //   }

  if (!reqApp?.alert?.displayAlert) reqApp.alert = { displayAlert: false };
  return res.status(200).json({ errCode: '0', payload: { app: { name: reqApp.name, displayName: reqApp.displayName, alert: reqApp.alert } } });
}

const getLeftMenuItemsDemo = (req, res) => {
  const leftMenuItems = [
    {
      heading: 'Heading 1',
      items: [
        { label: 'Item 1', route: '/1_1' },
        { label: 'Item 2', route: '/1_2' },
        {
          label: 'Item 3 (Expandable)', subItems: [
            { label: 'Subitem 1', route: '/1_3_s1' },
            { label: 'Subitem 2', route: '/1_3_s2', newTab: true },
            { label: 'Subitem 3', route: '/1_3_s3' },
          ]
        },
        { label: 'Item 4 (Opens in new tab)', route: '/1_4', newTab: true },
        {
          label: 'Item 5 (Expandable)', subItems: [
            { label: 'Subitem 1', route: '/1_5_s1' },
            { label: 'Subitem 2', route: '/1_5_s2' }
          ]
        },
        { label: 'Item 6', route: '/1_6' },
      ]
    },
    {
      heading: 'Heading 2',
      items: [
        { label: 'Item 1 (Opens in new tab)', route: '/2_1', newTab: true },
        { label: 'Item 2', route: '/2_2' },
        {
          label: 'Item 3 (Expandable)', subItems: [
            { label: 'Subitem 1', route: '/2_3_s1' },
            { label: 'Subitem 2', route: '/2_3_s2', newTab: true },
            { label: 'Subitem 2', route: '/2_3_s3' }
          ]
        },
        { label: 'Item 4', route: '/2_4' },
      ]
    },
    {
      heading: 'Heading 3',
      items: [
        { label: 'Item 1', route: '/3_1' },
        { label: 'Item 2 (Opens in new tab)', route: '/3_2', newTab: true },
        {
          label: 'Item 3 (Expandable)', subItems: [
            { label: 'Subitem 1', route: '/3_3_s1' },
            { label: 'Subitem 2', route: '/3_3_s2', newTab: true },
            { label: 'Subitem 2', route: '/3_3_s3' }
          ]
        },
        { label: 'Item 4', route: '/3_4' },
      ]
    }
  ];

  res.status(200).json({ errCode: '0', payload: { leftMenuItems: JSON.stringify(leftMenuItems) } });
}

const getLeftMenuItems = (req, res) => {
  const leftMenuItems = [
    {
      heading: 'Template App',
      items: [
        { label: 'Home', route: '/' }
      ]
    }
  ];

  res.status(200).json({ errCode: '0', payload: { leftMenuItems: JSON.stringify(leftMenuItems) } });
}

module.exports = { getAppAlert, getAppInfo, getLeftMenuItemsDemo, getLeftMenuItems };