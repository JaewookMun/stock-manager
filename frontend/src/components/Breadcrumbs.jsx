import { useState, useEffect, useCallback } from 'react';
import { useLocation } from 'react-router';
import Breadcrumb from 'react-bootstrap/Breadcrumb';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import menuItems from '../menu-items';

export default function Breadcrumbs() {
  const location = useLocation();

  const [main, setMain] = useState({});
  const [item, setItem] = useState({});

  const getCollapse = useCallback(
    (menuItem) => {
      if (menuItem.children) {
        menuItem.children.forEach((child) => {
          if (child.type === 'collapse') {
            getCollapse(child);
          } else if (child.type === 'item' && location.pathname === child.url) {
            setMain({ type: 'collapse', title: typeof menuItem.title === 'string' ? menuItem.title : undefined });
            setItem({ type: 'item', title: typeof child.title === 'string' ? child.title : undefined });
          }
        });
      }
    },
    [location.pathname]
  );

  useEffect(() => {
    setMain({});
    setItem({});
    menuItems.items.forEach((navItem) => {
      if (navItem.type === 'group') {
        getCollapse(navItem);
      }
    });
  }, [location.pathname, getCollapse]);

  if (item?.type !== 'item') return null;

  const title = item.title ?? '';

  return (
    <div className="page-header">
      <div className="page-block">
        <Row className="align-items-center">
          <Col className="page-header-title">
            <h5>{title}</h5>
          </Col>
          <Col xs="auto">
            <Breadcrumb listProps={{ style: { marginBottom: 0 } }}>
              <Breadcrumb.Item href="/">Home</Breadcrumb.Item>
              {main.title && (
                <Breadcrumb.Item href="#">{main.title}</Breadcrumb.Item>
              )}
              <Breadcrumb.Item href="#" active>{title}</Breadcrumb.Item>
            </Breadcrumb>
          </Col>
        </Row>
      </div>
    </div>
  );
}
