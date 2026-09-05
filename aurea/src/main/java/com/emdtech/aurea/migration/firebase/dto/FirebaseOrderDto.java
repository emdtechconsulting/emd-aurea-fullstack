package com.emdtech.aurea.migration.firebase.dto;

import java.util.List;
import java.util.Map;

public class FirebaseOrderDto {

    private String id;

    private Integer numeroPedido;

    private String cliente;

    // Formato antiguo
    private String fechaEntrega;
    private String horaEntrega;

    // Formato nuevo
    private String fecha;
    private String hora;

    private String direccion;
    private String distrito;

    /*
     * En el Firebase nuevo:
     * delivery = true / false
     */
    private Object delivery;

    /*
     * En el Firebase nuevo:
     * costoDelivery = 0, 10, etc.
     */
    private Object costoDelivery;

    private String observaciones;

    private String estado;

    /*
     * Ejemplos:
     * Pagado
     * Pendiente
     * Parcial
     */
    private String estadoPago;

    /*
     * En registros nuevos puede ser boolean:
     * true / false.
     *
     * Lo dejamos Object para soportar
     * distintas generaciones del JSON.
     */
    private Object pagado;

    /*
     * Nombre real encontrado en Firebase:
     * formaPago
     */
    private String formaPago;

    /*
     * Timestamps históricos.
     */
    private String fechaCreacion;
    private String fechaRegistro;
    private String ultimaActualizacion;

    private Object subtotal;
    private Object total;

    private List<Map<String, Object>> productos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(Integer numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(String horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public Object getDelivery() {
        return delivery;
    }

    public void setDelivery(Object delivery) {
        this.delivery = delivery;
    }

    public Object getCostoDelivery() {
        return costoDelivery;
    }

    public void setCostoDelivery(Object costoDelivery) {
        this.costoDelivery = costoDelivery;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Object getPagado() {
        return pagado;
    }

    public void setPagado(Object pagado) {
        this.pagado = pagado;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(
            String ultimaActualizacion) {

        this.ultimaActualizacion =
                ultimaActualizacion;
    }

    public Object getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Object subtotal) {
        this.subtotal = subtotal;
    }

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }

    public List<Map<String, Object>> getProductos() {
        return productos;
    }

    public void setProductos(
            List<Map<String, Object>> productos) {

        this.productos = productos;
    }
}