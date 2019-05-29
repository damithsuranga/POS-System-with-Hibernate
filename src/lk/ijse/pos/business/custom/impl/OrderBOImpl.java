package lk.ijse.pos.business.custom.impl;

import lk.ijse.pos.business.custom.OrderBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.DAOTypes;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.db.HibernateUtil;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.entity.Order;
import lk.ijse.pos.entity.OrderDetail;
import org.hibernate.Session;

import java.sql.Connection;

public class OrderBOImpl implements OrderBO {

    private OrderDAO orderDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER);
    private OrderDetailDAO orderDetailDAO = DAOFactory.getInstance().getDAO(DAOTypes.ORDER_DETAIL);
    private ItemDAO itemDAO = DAOFactory.getInstance().getDAO(DAOTypes.ITEM);
    private CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOTypes.CUSTOMER);


    @Override
    public void placeOrder(OrderDTO order) throws Exception {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();
            customerDAO.setSession(session);
            itemDAO.setSession(session);
            orderDAO.setSession(session);
            orderDetailDAO.setSession(session);

            Customer customer = customerDAO.find(order.getCustomerId());
            orderDAO.save(new Order(order.getOrderId(),order.getOrderDate(),customer));
            for(OrderDetailDTO dto : order.getOrderDetails()){
                orderDetailDAO.save(new OrderDetail(dto.getOrderId(),dto.getItemCode(),dto.getQty(),dto.getUnitPrice()));
                Item item = itemDAO.find(dto.getItemCode());
                int qty = item.getQtyOnHand()-dto.getQty();
                item.setQtyOnHand(qty);
            }
            session.getTransaction().commit();

        }
    }

    @Override
    public int generateOrderId() throws Exception {
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            orderDAO.setSession(session);
                    return orderDAO.getLastOrderId()+1;
        }catch (NullPointerException e){
            return 1;
        }
    }
}
