# -*- coding: mbcs -*-
#
# Abaqus/CAE Release 6.13-1 replay file
# Internal Version: 2013_05_16-04.28.56 126354
# Run by Filip on Sun Jun 07 14:05:27 2015
#

# from driverUtils import executeOnCaeGraphicsStartup
# executeOnCaeGraphicsStartup()
#: Executing "onCaeGraphicsStartup()" in the site directory ...
from abaqus import *
from abaqusConstants import *
session.Viewport(name='Viewport: 1', origin=(0.0, 0.0), width=278.755493164063, 
    height=173.609375)
session.viewports['Viewport: 1'].makeCurrent()
session.viewports['Viewport: 1'].maximize()
from caeModules import *
from driverUtils import executeOnCaeStartup
executeOnCaeStartup()
myModel = mdb.Model(name='Model A')
session.viewports['Viewport: 1'].setValues(displayedObject=None)
session.viewports['Viewport: 1'].partDisplay.geometryOptions.setValues(
    referenceRepresentation=ON)
#execfile('abaqusMacros.py', __main__.__dict__)
import allAbaqusMacros
allAbaqusMacros.partMacro()
allAbaqusMacros.materialMacro()
allAbaqusMacros.sectionMacro2()

