import React, { Fragment } from 'react';
import { Helmet } from 'react-helmet';
import { useSelector } from 'react-redux';

const HelmetComponent = ({ title, children }) => {
  const appInfo = useSelector((state) => state.app.appInfo);

  return (
    <Fragment>
      <Helmet>
        <title>{`${title ? (`${title} - `) : ""}${appInfo?.displayName ? appInfo.displayName : "GatorApps"}`}</title>
        <meta name="description" content="Template App is a template for apps under the GatorApps suit." />
      </Helmet>
      {children}
    </Fragment>
  )
};

export default HelmetComponent;