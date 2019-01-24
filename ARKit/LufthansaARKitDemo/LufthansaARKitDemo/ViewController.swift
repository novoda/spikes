//
//  ViewController.swift
//  LufthansaARKitDemo
//
//  Created by Meghan Kane on 9/17/18.
//  Copyright © 2018 Novoda. All rights reserved.
//

import ARKit

class ViewController: UIViewController {
    
    @IBOutlet weak var sceneView: ARSCNView!
    @IBOutlet weak var customSwitch: BetterSegmentedControl!
    @IBOutlet weak var containerView: UIView!
    
    private var airplane = SCNNode()
    private var cabin = SCNNode()
    private var imageAnchor: ARAnchor?
    private var imageNode: SCNNode?
    private var objectRendered: SCNNode?
    private let scale = 0.04
    private var animationInfo: AnimationInfo?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        sceneView.delegate = self
        sceneView.showsStatistics = false
        sceneView.scene = SCNScene()
        
        prepareObject(node: airplane, sceneName: "art.scnassets/plane-exterior/plane-exterior.scn")
        prepareObject(node: cabin, sceneName: "art.scnassets/plane-interior-reduced/cabin.scn")
        setupViews()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        runSession()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        sceneView.session.pause()
    }
    
    private func setupViews() {
        customizeSwitch()
        sceneView.autoenablesDefaultLighting = false
        setupLightSource()
    }
    
    private func runSession() {
        let configuration = ARWorldTrackingConfiguration()
        configuration.planeDetection = .horizontal
        
        if #available(iOS 12.0, *) {
            configuration.environmentTexturing = .automatic
        }
        
        guard let referenceImages = ARReferenceImage.referenceImages(inGroupNamed: "AR Resources", bundle: nil) else {
            fatalError("Missing expected asset catalog resources.")
        }
        
        configuration.detectionImages = referenceImages
        
        configuration.isLightEstimationEnabled = true
        
        
        sceneView.session.run(configuration, options: [.resetTracking, .removeExistingAnchors])
    }
    
    private func prepareObject(node: SCNNode, sceneName: String) {
        guard let scene = SCNScene(named: sceneName) else {
            print("Could not load scene!")
            return
        }
        
        let childNodes = scene.rootNode.childNodes
        for childNode in childNodes {
            node.addChildNode(childNode)
        }
        
        node.scale = SCNVector3(scale, scale, scale)
    }
    
    private func customizeSwitch() {
        let novodaBlue = UIColor(red: CGFloat(78.0/255.0), green: CGFloat(162.0/255.0), blue: CGFloat(214.0/255.0), alpha: CGFloat(0.9))
        customSwitch.segments = LabelSegment.segments(withTitles: ["Interior", "Exterior"],
                                                      normalFont: UIFont(name: "HelveticaNeue", size: 13.0)!,
                                                      normalTextColor: UIColor(white: 1.0, alpha: 0.5),
                                                      selectedFont: UIFont(name: "HelveticaNeue-Bold", size: 13.0)!,
                                                      selectedTextColor: novodaBlue)
        customSwitch.setIndex(1)
        customSwitch.options = [.backgroundColor(novodaBlue),
                                .indicatorViewBackgroundColor(.white),
                                .indicatorViewInset(4.0),
                                .cornerRadius(22.0)]
        customSwitch.addTarget(self, action: #selector(controlValueChanged(_:)), for: .valueChanged)
    }
    
    private func setupLightSource() {
        let ambientLightNode = SCNNode()
        ambientLightNode.light = SCNLight()
        ambientLightNode.light!.type = SCNLight.LightType.ambient
        ambientLightNode.light!.color = UIColor(white: 0.67, alpha: 1.0)
        sceneView.scene.rootNode.addChildNode(ambientLightNode)
        
        let omniLightNode = SCNNode()
        omniLightNode.light = SCNLight()
        omniLightNode.light!.type = SCNLight.LightType.omni
        omniLightNode.light!.color = UIColor(white: 0.75, alpha: 1.0)
        omniLightNode.position = SCNVector3Make(0, 50, 50)
        sceneView.scene.rootNode.addChildNode(omniLightNode)
    }
    
    @IBAction func resetExperience() {
        guard let objectRendered = objectRendered else {
            return
        }
        
        DispatchQueue.main.async {
            SCNTransaction.begin()
            self.containerView.alpha = 0
            SCNTransaction.animationDuration = 6.0
            if objectRendered == self.airplane {
                self.airplane.position = SCNVector3Make(3, 1, 0)
            } else if objectRendered == self.cabin {
                self.cabin.opacity = 0.0
            }
            SCNTransaction.completionBlock = {
                self.resetState()
            }
            SCNTransaction.commit()
        }
    }
    
    private func resetState() {
        customSwitch.setIndex(1)
        if let imageAnchor = imageAnchor {
            sceneView.session.remove(anchor: imageAnchor)
        }
        objectRendered = nil
    }
    
    @objc private func controlValueChanged(_ sender: BetterSegmentedControl) {
        if sender.index == 0 {
            transition(fromObject: airplane, toObject: cabin)
        } else {
            transition(fromObject: cabin, toObject: airplane)
        }
    }
    
    private func transition(fromObject: SCNNode, toObject: SCNNode) {
        let animationDuration = 3.0
        objectRendered = toObject
        
        DispatchQueue.main.async {
            SCNTransaction.begin()
            SCNTransaction.animationDuration = animationDuration

            fromObject.opacity = 0.0
            toObject.opacity = 1.0
            SCNTransaction.commit()
        }
    }
}

extension ViewController: ARSCNViewDelegate {
    
    func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor) {
        guard let imageAnchor = anchor as? ARImageAnchor, objectRendered == nil else { return }
        
        DispatchQueue.main.async {
            self.airplane.position = SCNVector3Make(-0.5, 0, 0)
            node.addChildNode(self.airplane)
            
            SCNTransaction.begin()
            SCNTransaction.animationDuration = 5
            self.airplane.position = SCNVector3Zero
            SCNTransaction.completionBlock = {
                SCNTransaction.begin()
                SCNTransaction.animationDuration = 2
                self.containerView.alpha = 1
                SCNTransaction.commit()
                
                self.objectRendered = self.airplane
                self.imageAnchor = imageAnchor
                self.imageNode = node
                self.cabin.position = SCNVector3Zero
                self.cabin.opacity = 0
                node.addChildNode(self.cabin)
            }
            SCNTransaction.commit()
        }
    }
    
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        
        guard let imageNode = imageNode else {
            return
        }

        // 1. Unwrap animationInfo. Calculate animationInfo if it is nil.
        guard let animationInfo = animationInfo else {
            refreshAnimationVariables(startTime: time,
                                      initialPosition: airplane.simdWorldPosition,
                                      finalPosition: imageNode.simdWorldPosition,
                                      initialOrientation: airplane.simdWorldOrientation,
                                      finalOrientation: imageNode.simdWorldOrientation)
            return
        }
        
        // 2. Calculate new animationInfo if image position or orientation changed.
        if !simd_equal(animationInfo.finalModelPosition, imageNode.simdWorldPosition) ||
            animationInfo.finalModelRotation != imageNode.simdWorldOrientation {
            
            refreshAnimationVariables(startTime: time,
                                      initialPosition: airplane.simdWorldPosition,
                                      finalPosition: imageNode.simdWorldPosition,
                                      initialOrientation: airplane.simdWorldOrientation,
                                      finalOrientation: imageNode.simdWorldOrientation)
        }
        
        // 3. Calculate interpolation based on passedTime/totalTime ratio.
        let passedTime = time - animationInfo.startTime
        var t = min(Float(passedTime/animationInfo.duration), 1)
        // Applying curve function to time parameter to achieve "ease out" timing
        t = sin(t * .pi * 0.5)
        
        // 4. Calculate and set new model position and orientation.
        let f3t = simd_make_float3(t, t, t)
        airplane.simdWorldPosition = simd_mix(animationInfo.initialModelPosition, animationInfo.finalModelPosition, f3t)
        airplane.simdWorldOrientation = simd_slerp(animationInfo.initialModelRotation, animationInfo.finalModelRotation, t)
    }
    
    func refreshAnimationVariables(startTime: TimeInterval, initialPosition: float3, finalPosition: float3, initialOrientation: simd_quatf, finalOrientation: simd_quatf) {
        let distance = simd_distance(initialPosition, finalPosition)
        // Average speed of movement is 0.15 m/s.
        let speed = Float(0.15)
        // Total time is calculated as distance/speed. Min time is set to 0.1s and max is set to 2s.
        let animationDuration = Double(min(max(0.1, distance/speed), 2))
        // Store animation information for later usage.
        animationInfo = AnimationInfo(startTime: startTime,
                                      duration: animationDuration,
                                      initialModelPosition: initialPosition,
                                      initialModelRotation: initialOrientation, finalModelPosition: finalPosition,
                                      finalModelRotation: finalOrientation)
    }
    
    func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor) {
        if anchor == self.imageAnchor {


        }
    }
}

struct AnimationInfo {
    var startTime: TimeInterval
    var duration: TimeInterval
    var initialModelPosition: simd_float3
    var initialModelRotation: simd_quatf
    var finalModelPosition: simd_float3
    var finalModelRotation: simd_quatf
}
