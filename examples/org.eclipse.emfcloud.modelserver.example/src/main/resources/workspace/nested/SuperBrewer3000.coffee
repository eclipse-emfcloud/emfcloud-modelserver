<?xml version="1.0" encoding="UTF-8"?>
<org.eclipse.emfcloud.modelserver.coffee.model:Machine
    xmi:version="2.0"
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:org.eclipse.emfcloud.modelserver.coffee.model="http://www.eclipsesource.com/modelserver/example/coffeemodel"
    xsi:schemaLocation="http://www.eclipsesource.com/modelserver/example/coffeemodel Coffee.ecore"
    name="Super Brewer 3000">
  <children
      xsi:type="org.eclipse.emfcloud.modelserver.coffee.model:BrewingUnit"/>
  <children
      xsi:type="org.eclipse.emfcloud.modelserver.coffee.model:ControlUnit">
    <processor
        clockSpeed="5"
        numberOfCores="10"
        socketconnectorType="Z51"
        thermalDesignPower="100"/>
    <display
        width="10"
        height="20"/>
  </children>
  <workflows name="Simple Workflow">
    <nodes xsi:type="org.eclipse.emfcloud.modelserver.coffee.model:AutomaticTask"
        name="PreHeat"
        component="//@children.0"/>
  </workflows>
</org.eclipse.emfcloud.modelserver.coffee.model:Machine>
